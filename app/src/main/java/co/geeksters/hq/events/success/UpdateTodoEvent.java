package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class UpdateTodoEvent {

    Todo updated_todo;

    public UpdateTodoEvent(Todo updated_todo) {
        this.updated_todo = updated_todo;
    }
}
