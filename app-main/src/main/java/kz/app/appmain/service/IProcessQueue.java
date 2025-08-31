package kz.app.appmain.service;

/**
 * Интерфейс IProcessQueue нужен исключительно потому, что
 * 1) MessageServiceAsync реализует IMessageSend, но
 * 2) аннотация @Async хочет создать прокси вокруг метода, а когда @EventListener пытается
 * вызвать этот метод, в интерфейсе IMessageSend метода не обнаруживается.
 * Поэтому мы искусственно добавляем метод processQueue в интерфейс.
 */
public interface IProcessQueue {
    void processQueue();
}
