package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class TodoEvent {

    public Todo todo;

    public TodoEvent(Todo todo) {
        this.todo = todo;
    }
}
