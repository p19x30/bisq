package io.bitsquare.p2p.storage.data;

import com.google.common.annotations.VisibleForTesting;
import io.bitsquare.p2p.storage.ProtectedExpirableDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.Date;

public class ProtectedData implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ProtectedExpirableDataStorage.class);

    public final ExpirablePayload expirablePayload;
    transient public long ttl;
    public final PublicKey ownerStoragePubKey;
    public final int sequenceNumber;
    public final byte[] signature;
    @VisibleForTesting
    public Date date;

    public ProtectedData(ExpirablePayload expirablePayload, long ttl, PublicKey ownerStoragePubKey, int sequenceNumber, byte[] signature) {
        this.expirablePayload = expirablePayload;
        this.ttl = ttl;
        this.ownerStoragePubKey = ownerStoragePubKey;
        this.sequenceNumber = sequenceNumber;
        this.signature = signature;
        this.date = new Date();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            ttl = expirablePayload.getTTL();

            // in case the reported creation date is in the future 
            // we reset the date to the current time
            if (date.getTime() > new Date().getTime()) {
                log.warn("Date of object is in future. " +
                        "That might be ok as clocks are not synced but could be also a spam attack. " +
                        "date=" + date + " / now=" + new Date());
                date = new Date();
            }
            date = new Date();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean isExpired() {
        return (new Date().getTime() - date.getTime()) > ttl;
    }

    @Override
    public String toString() {
        return "ProtectedData{" +
                "data=\n" + expirablePayload +
                ", \nttl=" + ttl +
                ", sequenceNumber=" + sequenceNumber +
                ", date=" + date +
                "\n}";
    }
}