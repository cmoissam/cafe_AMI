package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class TodoEvent {

    public Todo todo;

    public TodoEvent(Todo todo) {
        this.todo = todo;
    }
}
