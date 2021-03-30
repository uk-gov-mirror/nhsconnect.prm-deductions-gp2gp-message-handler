package uk.nhs.prm.deductions.gp2gpmessagehandler.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class DownloadUnhandledMessages {

    private static Logger logger = LogManager.getLogger(DownloadUnhandledMessages.class);

    @JmsListener(destination = "${activemq.unhandledQueue}")
    public void onMessage(Message message) throws JMSException, IOException {
        BytesMessage bytesMessage = (BytesMessage) message;
        byte[] contentAsBytes = new byte[(int) bytesMessage.getBodyLength()];
        bytesMessage.readBytes(contentAsBytes);

        File file = new File(UUID.randomUUID().toString() + ".txt");
        FileOutputStream fos = new FileOutputStream(file);

        logger.error("Downloaded message to " + file.getAbsolutePath());

        fos.write(contentAsBytes);
    }
}
