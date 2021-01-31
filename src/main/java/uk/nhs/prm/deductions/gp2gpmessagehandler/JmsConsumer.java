package uk.nhs.prm.deductions.gp2gpmessagehandler;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.stream.XMLInputFactory;


@Component
public class JmsConsumer {

    final
    JmsTemplate jmsTemplate;

    @Value("${activemq.outboundQueue}")
    private String outboundQueue;

    public JmsConsumer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    //might need to use BytesMessage as that's how is probably gonna be read from the q
    @JmsListener(destination = "${activemq.inboundQueue}")
    public void onMessage(TextMessage message) {
        try {
            ByteArrayDataSource dataSource = new ByteArrayDataSource(message.getText(), "multipart/mixed");
            MimeMultipart mimeMultipart = new MimeMultipart(dataSource);
            System.out.println(mimeMultipart.getCount());
            BodyPart soapHeader = mimeMultipart.getBodyPart(0);
            XMLInputFactory input = new WstxInputFactory();
            input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
            SOAPEnvelope soapEnvelope = xmlMapper.readValue(soapHeader.getInputStream(), SOAPEnvelope.class);
            System.out.println(soapEnvelope.header.action);

//            jmsTemplate.convertAndSend(outboundQueue, message.getText());
        } catch(Exception e) {
            System.out.println("Received Exception : "+ e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SOAPEnvelope {
        @JacksonXmlProperty(localName = "SOAP-ENV:Header")
        SOAPHeader header;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SOAPHeader {
        @JacksonXmlProperty(localName = "eb:Action")
        String action;
    }
}
