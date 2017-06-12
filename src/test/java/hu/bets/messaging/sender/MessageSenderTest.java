package hu.bets.messaging.sender;

import com.rabbitmq.client.Channel;
import hu.bets.model.ProcessingResult;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.CompletionService;

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
}