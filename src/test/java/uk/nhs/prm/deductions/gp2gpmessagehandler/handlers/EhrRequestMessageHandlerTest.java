package uk.nhs.prm.deductions.gp2gpmessagehandler.handlers;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import uk.nhs.prm.deductions.gp2gpmessagehandler.gp2gpMessageModels.ParsedMessage;
import uk.nhs.prm.deductions.gp2gpmessagehandler.gp2gpMessageModels.SOAPEnvelope;

import javax.jms.JMSException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
/* here we list classes that we want to be instantiated in the test */
@SpringBootTest(classes = { EhrRequestMessageHandler.class })
public class EhrRequestMessageHandlerTest {
    @Autowired
    EhrRequestMessageHandler messageHandler;

    @MockBean
    JmsTemplate mockJmsTemplate;

    @Value("${activemq.outboundQueue}")
    String outboundQueue;

    private ActiveMQBytesMessage getActiveMQBytesMessage() throws JMSException {
        ActiveMQBytesMessage bytesMessage = new ActiveMQBytesMessage();
        bytesMessage.writeBytes(new byte[10]);
        bytesMessage.reset();
        return bytesMessage;
    }

    @Test
    public void shouldReturnCorrectInteractionId() {
        assertThat(messageHandler.getInteractionId(), equalTo("RCMR_IN010000UK05"));
    }

    @Test
    public void shouldPutEhrRequestMessagesOnJSQueue() throws JMSException {
        SOAPEnvelope envelope = new SOAPEnvelope();
        ParsedMessage parsedMessage = new ParsedMessage(envelope);
        ActiveMQBytesMessage bytesMessage = getActiveMQBytesMessage();

        messageHandler.handleMessage(parsedMessage, bytesMessage);
        verify(mockJmsTemplate, times(1)).convertAndSend("outboundQueue", bytesMessage);
    }
}
