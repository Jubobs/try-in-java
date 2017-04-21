package com.jubobs.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.jubobs.util.NonFatal.isNonFatal;

public final class Success<T> extends Try<T> {

    private final T value;

    Success(T value) {
        this.value = value;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public void ifSuccess(Consumer<? super T> action) {
        action.accept(value);
    }

    @Override
    public void ifSuccessOrElse(Consumer<? super T> action, Runnable failsafe) {
        action.accept(value);
    }

    @Override
    public Try<T> filter(Predicate<? super T> predicate) {
        if (predicate.test(value)) {
            return this;
        } else {
            return new Failure<>(new NoSuchElementException("Predicate does not hold for " + value));
        }

    }

    @Override
    public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        return Try.fallible(() -> mapper.apply(value));
    }

    @Override
    public <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper) {
        try {
            @SuppressWarnings("unchecked")
            Try<U> r = (Try<U>) mapper.apply(value);
            return r;
        } catch (Throwable e) {
            if (isNonFatal(e)) {
                return new Failure<>(e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(value);
    }

    @Override
    public T orElseGet(Supplier<? extends T> supplier) {
        return value;
    }

    @Override
    public Try<T> or(Supplier<? extends Try<? extends T>> supplier) {
        return this;
    }

    @Override
    public Try<Throwable> invert() {
        return new Failure<>(new UnsupportedOperationException("Success.failed"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Success)) {
            return false;
        }

        Success<?> other = (Success<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Success[" + value + "]";
    }

}
