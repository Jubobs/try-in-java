package com.jubobs.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Try<T> {

    private final T value;

    private final Throwable throwable;

    public static <T> Try<T> fallible(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);

        try {
            return new Try<>(supplier.get(), null);
        } catch (Throwable e) {
            if (nonFatal(e)) {
                return new Try<>(null, e);
            } else {
                throw e;
            }
        }
    }

    public static <T> Try<T> failure(Throwable exception) {
        Objects.requireNonNull(exception);
        return new Try<>(null, exception);
    }

    public static <T> Try<T> success(T value) {
        return new Try<>(value, null);
    }

    private Try(T value, Throwable throwable) {
        assert value == null || throwable == null
                : "value and throwable cannot both be null";

        this.value = value;
        this.throwable = throwable;
    }

    public Optional<T> toOptional(){
        if (isFailure()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(value);
        }
    }

    private static boolean nonFatal(Throwable e) {
        return !(e instanceof VirtualMachineError ||
                e instanceof ThreadDeath ||
                e instanceof LinkageError);
    }

    public boolean isFailure() {
        return throwable != null;
    }

    public boolean isSuccess() {
        return throwable == null;
    }

    public void ifSuccess(Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(value);
        }
    }

    public void ifSuccessOrElse(Consumer<? super T> action, Runnable failsafe) {
        if (isSuccess()) {
            action.accept(value);
        } else {
            failsafe.run();
        }
    }

    public Try<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (isFailure() || predicate.test(value)) {
            return this;
        } else {
            return new Try<>(null, new NoSuchElementException("Predicate does not hold for " + value));
        }
    }

    public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isFailure()) {
            @SuppressWarnings("unchecked")
            Try<U> r = (Try<U>) this;
            return r;
        } else {
            return Try.fallible(() -> mapper.apply(value));
        }
    }


    public <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isFailure()) {
            @SuppressWarnings("unchecked")
            Try<U> r = (Try<U>) this;
            return r;
        } else {
            try {
                @SuppressWarnings("unchecked")
                Try<U> r = (Try<U>) mapper.apply(value);
                return r;
            } catch (Throwable e) {
                if (nonFatal(e)) {
                    return new Try<>(null, e);
                } else {
                    throw e;
                }
            }
        }
    }

    public Stream<T> stream() {
        if (isFailure()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return isSuccess()
                ? value
                : supplier.get();
    }

    public Try<T> or(Supplier<? extends Try<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isSuccess()) {
            return this;
        } else {
            try {
                @SuppressWarnings("unchecked")
                Try<T> r = (Try<T>) supplier.get();
                return r;
            } catch (Throwable e) {
                if (nonFatal(e)) {
                    return new Try<>(null, e);
                } else {
                    throw e;
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Try)) {
            return false;
        }

        Try<?> other = (Try<?>) obj;
        return isFailure()
                ? Objects.equals(throwable, other.throwable)
                : Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, throwable);
    }

    @Override
    public String toString() {
        return isFailure()
                ? String.format("Failure[%s]", throwable)
                : String.format("Success[%s]", value);
    }

}