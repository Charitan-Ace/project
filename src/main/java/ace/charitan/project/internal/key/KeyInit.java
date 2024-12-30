package ace.charitan.project.internal.key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class KeyInit {

    @Autowired
    private KeyService keyService;

    @EventListener(ContextRefreshedEvent.class)
    void contextRefreshedEvent() {
        // keyService.updateSignatureKey();
        // keyService.updateEncryptionKey();
    }

}
