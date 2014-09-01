package ru.mmaximov.smsalert.biz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MessageFilterTest {
    private Tuple<MessageFilter, Message> input;
    private boolean expected;

    @Parameterized.Parameters
    public static Collection getTestCases() {
        return Arrays.asList(new Object[][]{
                //first arg is null
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "code", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "code", MessageFilter.FilterType.And), new Message("+47781123", "Your code is 123")), true},
                //second arg is null
                {new Tuple<MessageFilter, Message>(new MessageFilter("\\d+", null, MessageFilter.FilterType.Or), new Message("+47781123", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("\\d+", null, MessageFilter.FilterType.And), new Message("+47781123", " Your code is 123")), true},
                //regex matching
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "bullshit", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), false},
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "bullshit", MessageFilter.FilterType.And), new Message("+47781123", "Your code is 123")), false},
                //start-end terminals
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "^Your.*", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), true},
                //case-insensitive
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "^your.*", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "^your$", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), false},
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "^your", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), false},
                {new Tuple<MessageFilter, Message>(new MessageFilter(null, "Your code is \\d+$", MessageFilter.FilterType.Or), new Message("+47781123", "Your code is 123")), true},
                //phone matching
                {new Tuple<MessageFilter, Message>(new MessageFilter("\\d+", "code", MessageFilter.FilterType.Or), new Message("+47781123", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("\\d+", "code", MessageFilter.FilterType.And), new Message("+47781123", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("^\\+47.*", null, MessageFilter.FilterType.Or), new Message("+47781123", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("^\\d{4}$", null, MessageFilter.FilterType.Or), new Message("1234", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("^\\d{4}$", null, MessageFilter.FilterType.Or), new Message("12345", " Your code is 123")), false},
                //and/or mode
                {new Tuple<MessageFilter, Message>(new MessageFilter("bullshit", "code", MessageFilter.FilterType.Or), new Message("+47781123", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("bullshit", "code", MessageFilter.FilterType.And), new Message("+47781123", " Your code is 123")), false},
                {new Tuple<MessageFilter, Message>(new MessageFilter("\\d+", "bullshit", MessageFilter.FilterType.Or), new Message("+47781123", " Your code is 123")), true},
                {new Tuple<MessageFilter, Message>(new MessageFilter("\\d+", "bullshit", MessageFilter.FilterType.And), new Message("+47781123", " Your code is 123")), false}
        });
    }

    public MessageFilterTest(Tuple<MessageFilter, Message> input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testMatches() throws Exception {
        assertEquals(input.first.matches(input.second), expected);
    }

    private static class Tuple<X, Y> {
        public final X first;
        public final Y second;
        public Tuple(X first, Y second) {
            this.first = first;
            this.second = second;
        }
    }
}