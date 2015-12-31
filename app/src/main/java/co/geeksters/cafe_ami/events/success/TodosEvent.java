package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class TodosEvent {

    public List<Todo> todos;

    public TodosEvent(List<Todo> todos) {
        this.todos = todos;
    }
}
