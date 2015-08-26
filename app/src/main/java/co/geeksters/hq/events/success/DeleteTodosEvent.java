package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Todo;

/**
 * Created by geeksters on 11/08/15.
 */
public class DeleteTodosEvent {

    public List<Todo> todos;

    public DeleteTodosEvent(List<Todo> todos) {
        this.todos = todos;
    }

}
