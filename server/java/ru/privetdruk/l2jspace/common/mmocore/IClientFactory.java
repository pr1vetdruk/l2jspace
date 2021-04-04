package ru.privetdruk.l2jspace.common.mmocore;

public interface IClientFactory<T extends MMOClient<?>> {
    public T create(final MMOConnection<T> con);
}