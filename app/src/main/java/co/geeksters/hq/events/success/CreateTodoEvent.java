package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class CreateTodoEvent {

    public Todo created_todo;

    public CreateTodoEvent(Todo created_todo) {
        this.created_todo = created_todo;
    }
}
