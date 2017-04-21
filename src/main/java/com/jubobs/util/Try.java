package com.jubobs.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.jubobs.util.NonFatal.isNonFatal;

public abstract class Try<T> {

    // package-private constructor
    Try() {}

    public static <T> Try<T> fallible(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);

        try {
            return new Success<>(supplier.get());
        } catch (Throwable e) {
            if (isNonFatal(e)) {
                return new Failure<>(e);
            } else {
                throw e;
            }
        }
    }

    public static <T> Try<T> failure(Throwable exception) {
        return new Failure<>(exception);
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    public abstract Optional<T> toOptional();

    public abstract boolean isFailure();

    public abstract boolean isSuccess();

    public abstract void ifSuccess(Consumer<? super T> action);

    public abstract void ifSuccessOrElse(Consumer<? super T> action, Runnable failsafe);

    public abstract Try<T> filter(Predicate<? super T> predicate);

    public abstract <U> Try<U> map(Function<? super T, ? extends U> mapper);

    public abstract <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper);

    public abstract Stream<T> stream();

    public abstract T orElseGet(Supplier<? extends T> supplier);

    public abstract Try<T> or(Supplier<? extends Try<? extends T>> supplier);

}
