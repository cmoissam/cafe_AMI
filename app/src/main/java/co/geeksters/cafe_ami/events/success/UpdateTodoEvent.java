package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class UpdateTodoEvent {

    Todo updated_todo;

    public UpdateTodoEvent(Todo updated_todo) {
        this.updated_todo = updated_todo;
    }
}
