package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Todo;

/**
 * Created by soukaina on 27/11/14.
 */
public class ListTodosForMemberEvent {

    List<Todo> todos_for_member;

    public ListTodosForMemberEvent(List<Todo> todos_for_member) {
        this.todos_for_member = todos_for_member;
    }
}
