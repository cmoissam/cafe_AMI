package co.geeksters.hq.bus;

import android.test.InstrumentationTestCase;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.events.success.EmptyMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;

/**
 * Created by soukaina on 03/12/14.
 */
public class BusRegistrationToMemberEventsTest extends InstrumentationTestCase {

    Bus bus;
    Member member;
    List<Member> members;

    @Before
    public void setUp() {
        // If we are not concerned on which thread interaction is occurring,
        // we instantiate a bus instance with ThreadEnforcer.ANY.
        bus = new Bus(ThreadEnforcer.ANY);

        HashMap<String, String> socials = new HashMap<String, String>();
        socials.put("facebook", "http://facebook.com");
        socials.put("twitter", "http://twitter.com");

        ArrayList<Todo> todos = new ArrayList<Todo>();
        Todo todo = new Todo(1, "todo content", 2014120418);
        todos.add(todo);

        ArrayList<Interest> interests = new ArrayList<Interest>();
        Interest interest = new Interest(1, "interest name");
        interests.add(interest);

        members = new ArrayList<Member>();

        member = new Member();
        /*member = new Member(1, "test full name", "test@email.com", "password", "password", "token", true,
                            false, socials, todos, interests, null, null, null, null);*/
        members.add(member);

        Member member_second = new Member();
        /*Member member_second = new Member(2, "test full name", "test_second@email.com", "password_second",
                "password_second", "token_second", true, true, socials, todos, interests, null, null, null, null);*/
        members.add(member_second);
    }

    public void testGetMembers() {
        // Register an object
        // to validate the event posted
        bus.register(new Object() {
            @Subscribe
            public void onGetMembersEvent(MembersEvent event) {
                assertEquals("test@email.com", event.members.get(0).email);
                assertEquals("test_second@email.com", event.members.get(1).email);
            }
        });

        bus.post(new MembersEvent(members));
    }

    public void testGetMember() {
        // Register an object
        // to validate the event posted
        bus.register(new Object() {
            @Subscribe
            public void onGetMemberEvent(MemberEvent event) {
                //assertEquals("test full name", event.member.full_nameee);
            }
        });

        bus.post(new MemberEvent(member));
    }

    public void testDeleteMember() {
        // Register an object
        // to validate the event posted
        bus.register(new Object() {
            @Subscribe
            public void onDeleteMemberEvent(EmptyMemberEvent event) {
                assertEquals(true, event.inEvent);
            }
        });

        bus.post(new EmptyMemberEvent());
    }
}