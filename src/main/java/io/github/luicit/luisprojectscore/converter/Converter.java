package io.github.luicit.luisprojectscore.converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Conversor base entre entidades y modelos.
 *
 * <p>Se implementa con funciones para evitar obligar a las clases hijas
 * a sobreescribir métodos cuando no se desea. Si en algún caso se requiere
 * lógica adicional, la clase hija puede seguir sobreescribiendo los métodos.</p>
 */
public class Converter<E, M> {

    private final Function<M, E> toEntityFunction;
    private final Function<E, M> toModelFunction;

    public Converter(
            Function<M, E> toEntityFunction,
            Function<E, M> toModelFunction
    ) {
        this.toEntityFunction = Objects.requireNonNull(toEntityFunction, "toEntityFunction is required");
        this.toModelFunction = Objects.requireNonNull(toModelFunction, "toModelFunction is required");
    }

    public E toEntity(M model) {
        return toEntityFunction.apply(model);
    }

    public M toModel(E entity) {
        return toModelFunction.apply(entity);
    }

    public List<E> toEntityList(List<M> models) {
        if (models == null || models.isEmpty()) {
            return Collections.emptyList();
        }

        return models.stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .toList();
    }

    public List<M> toModelList(List<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toModel)
                .toList();
    }

}
