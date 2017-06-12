package hu.bets.messaging.sender;

import com.google.common.collect.Sets;
import com.rabbitmq.client.Channel;
import hu.bets.model.ProcessingResult;
import hu.bets.utils.JsonUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;
import java.util.concurrent.CompletionService;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderTest {

    private MessageSender sut;

    @Mock
    private Channel channel;
    @Mock
    private CompletionService<ProcessingResult> completionService;

    @Before
    public void setup() {
        sut = new MessageSender(channel, completionService);
    }

    @Test
    public void a() {
        // placeholder only.
        assertTrue(true);
    }
}