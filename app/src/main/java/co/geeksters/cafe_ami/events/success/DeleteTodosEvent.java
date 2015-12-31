package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Todo;

/**
 * Created by geeksters on 11/08/15.
 */
public class DeleteTodosEvent {

    public List<Todo> todos;

    public DeleteTodosEvent(List<Todo> todos) {
        this.todos = todos;
    }

}
