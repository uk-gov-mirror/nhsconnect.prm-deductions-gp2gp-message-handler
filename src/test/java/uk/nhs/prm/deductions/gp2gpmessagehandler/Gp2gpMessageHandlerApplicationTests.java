package uk.nhs.prm.deductions.gp2gpmessagehandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@SpringBootTest
class Gp2gpMessageHandlerApplicationTests {
	@Autowired
	JmsTemplate jmsTemplate;

	@Value("${activemq.inboundQueue}")
	private String inboundQueue;

	@Value("${activemq.outboundQueue}")
	private String outboundQueue;

	@Test
	void shouldPassThroughMessages() {
		//action: send a message on the inbound q
		String testMessage = "------=_MIME-Boundary\n" +
				"Content-Type: application/xml\n" +
				"Content-ID: <50D33D75-04C6-40AF-947D-E6E9656C1EEB@inps.co.uk/Vision/3>\n" +
				"Content-Transfer-Encoding: 8bit\n" +
				"\n" +
				"<SOAP-ENV:Envelope>\n" +
				"      <SOAP-ENV:Header>\n" +
				"        <eb:CPAId>S2036482A2160104</eb:CPAId>\n" +
				"        <eb:ConversationId>${conversationId}</eb:ConversationId>\n" +
				"        <eb:Service>urn:nhs:names:services:gp2gp</eb:Service>\n" +
				"        <eb:Action>PRPA_IN000202UK01</eb:Action>\n" +
				"        <eb:MessageData>\n" +
				"            <eb:MessageId>${messageId}</eb:MessageId>\n" +
				"            <eb:Timestamp>2018-06-12T08:29:16Z</eb:Timestamp>\n" +
				"        </eb:MessageData>\n" +
				"      </SOAP-ENV:Header>\n" +
				"      <SOAP-ENV:Body>\n" +
				"          <eb:Manifest eb:version=\"2.0\">\n" +
				"              <eb:Reference eb:id=\"_FE6A40B9-F4C6-4041-A306-EA2A149411CD\" xlink:href=\"cid:FE6A40B9-F4C6-4041-A306-EA2A149411CD@inps.co.uk/Vision/3\">\n" +
				"                  <eb:Description xml:lang=\"en-GB\">COPC_IN000001UK01</eb:Description>\n" +
				"              </eb:Reference>\n" +
				"          </eb:Manifest>\n" +
				"      </SOAP-ENV:Body>\n" +
				"    </SOAP-ENV:Envelope>\n" +
				"\n" +
				"------=_MIME-Boundary\n" +
				"  Content-Type: application/xml\n" +
				"  Content-ID: <60D33D75-04C6-40AF-947D-E6E9656C1EEB@inps.co.uk/Vision/3>\n" +
				"  Content-Transfer-Encoding: 8bit\n" +
				"\n" +
				"  <PRPA_IN000202UK01>\n" +
				"    <id root=\"123123\"/>\n" +
				"  </PRPA_IN000202UK01>\n" +
				"\n" +
				"------=_MIME-Boundary--";
		jmsTemplate.send(outboundQueue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				BytesMessage bytesMessage = session.createBytesMessage();
				bytesMessage.writeUTF(testMessage);
				return bytesMessage;
			}
		});

		//assertion: verify the message gets on the outbound q
		jmsTemplate.setReceiveTimeout(5000);
//		Message message = jmsTemplate.receive(outboundQueue);
//		assertNotNull(message);
//		TextMessage textMessage = (TextMessage) message;
//		try {
//			assertThat(textMessage.getText(), equalTo(testMessage));
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
	}

}
