package ru.phyllosedis.textario.resource;

public interface Type<TypeConcrete extends Enum<TypeConcrete> & Type<TypeConcrete>> {

    TypeConcrete getUndefined();

    default TypeConcrete getByOrdinal(int ordinal) {
        Class<TypeConcrete> enumClass = (Class<TypeConcrete>) this.getClass();

        TypeConcrete[] constants = enumClass.getEnumConstants();

        if (ordinal < 0 || ordinal >= constants.length) {
            return getUndefined();
        }
        return constants[ordinal];
    }
}
