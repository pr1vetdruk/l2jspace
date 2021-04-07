package ru.privetdruk.l2jspace.common.mmocore;

public interface IMMOExecutor<T extends MMOClient<?>> {
    public void execute(ReceivablePacket<T> packet);
}