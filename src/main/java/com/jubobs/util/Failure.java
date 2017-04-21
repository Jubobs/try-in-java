package com.jubobs.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static com.jubobs.util.NonFatal.isNonFatal;

public final class Failure<T> extends Try<T> {

    private final Throwable throwable;

    public Failure(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public void ifSuccess(Consumer<? super T> action) {
    }

    @Override
    public void ifSuccessOrElse(Consumer<? super T> action, Runnable failsafe) {
        failsafe.run();
    }

    @Override
    public Try<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        @SuppressWarnings("unchecked")
        Try<U> r = (Try<U>) this;
        return r;
    }

    @Override
    public <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper) {
        @SuppressWarnings("unchecked")
        Try<U> r = (Try<U>) this;
        return r;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public T orElseGet(Supplier<? extends T> supplier) {
        return supplier.get();
    }

    @Override
    public Try<T> or(Supplier<? extends Try<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        try {
            @SuppressWarnings("unchecked")
            Try<T> r = (Try<T>) supplier.get();
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
    public Try<Throwable> invert() {
        return new Success<>(throwable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Failure)) {
            return false;
        }

        Failure<?> other = (Failure<?>) obj;
        return Objects.equals(throwable, other.throwable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(throwable);
    }

    @Override
    public String toString() {
        return "Failure[" + throwable + "]";
    }


}
