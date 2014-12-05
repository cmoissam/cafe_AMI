package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class DeleteTodoEvent {

    Todo deleted_todo;

    public DeleteTodoEvent(Todo deleted_todo) {
        this.deleted_todo = deleted_todo;
    }
}
