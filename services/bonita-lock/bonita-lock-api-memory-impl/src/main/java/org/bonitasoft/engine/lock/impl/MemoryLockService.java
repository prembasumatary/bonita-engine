/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.lock.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.bonitasoft.engine.lock.BonitaLock;
import org.bonitasoft.engine.lock.LockService;
import org.bonitasoft.engine.lock.SLockException;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.sessionaccessor.ReadSessionAccessor;
import org.bonitasoft.engine.sessionaccessor.TenantIdNotSetException;

/**
 * This service must be configured as a singleton.
 * 
 * @author Elias Ricken de Medeiros
 * @author Baptiste Mesta
 */
public class MemoryLockService implements LockService {

    protected static final String SEPARATOR = "_";

    private final Map<String, ReentrantLock> locks = new HashMap<String, ReentrantLock>();

    protected final TechnicalLoggerService logger;

    protected final int lockTimeout;

    private final ReadSessionAccessor sessionAccessor;

    private final Object mutex = new Object();

    protected final boolean debugEnable;

    private final boolean traceEnable;

    /**
     * 
     * @param logger
     * @param sessionAccessor
     * @param lockTimeout
     *            timeout to obtain a lock in seconds
     */
    public MemoryLockService(final TechnicalLoggerService logger, final ReadSessionAccessor sessionAccessor, final int lockTimeout) {
        this.logger = logger;
        this.sessionAccessor = sessionAccessor;
        this.lockTimeout = lockTimeout;
        debugEnable = logger.isLoggable(getClass(), TechnicalLogSeverity.DEBUG);
        traceEnable = logger.isLoggable(getClass(), TechnicalLogSeverity.TRACE);
    }

    protected ReentrantLock getLock(final String key) {
        if (!locks.containsKey(key)) {
            // use fair mode?
            locks.put(key, new ReentrantLock());
        }
        return locks.get(key);
    }

    protected void removeLockFromMapIfnotUsed(final String key) {
        ReentrantLock reentrantLock = locks.get(key);
        /*
         * The reentrant lock must not have waiting thread that try to lock it, nor a lockservice.lock that locked it nor rejectedlockhandlers waiting for it
         */
        if (reentrantLock != null && !reentrantLock.hasQueuedThreads() && !reentrantLock.isLocked()) {
            if (debugEnable) {
                logger.log(getClass(), TechnicalLogSeverity.DEBUG, "removed from map " + reentrantLock.hashCode() + " id=" + key);
            }
            locks.remove(key);
        }
    }

    private String buildKey(final long objectToLockId, final String objectType) {
        try {
            return objectType + SEPARATOR + objectToLockId + SEPARATOR + sessionAccessor.getTenantId();
        } catch (TenantIdNotSetException e) {
            throw new IllegalStateException("Tenant not set");
        }
    }

    @Override
    public void unlock(final BonitaLock lock) throws SLockException {
        final String key = buildKey(lock.getObjectToLockId(), lock.getObjectType());
        if (traceEnable) {
            logger.log(getClass(), TechnicalLogSeverity.TRACE, "will unlock " + lock.getLock().hashCode() + " id=" + key);
        }
        synchronized (mutex) {
            try {
                lock.getLock().unlock();
                if (traceEnable) {
                    logger.log(getClass(), TechnicalLogSeverity.TRACE, "unlock " + lock.getLock().hashCode() + " id=" + key);
                }
            } finally {
                removeLockFromMapIfnotUsed(key);
            }
        }
    }

    @Override
    public BonitaLock tryLock(final long objectToLockId, final String objectType, final long timeout, final TimeUnit timeUnit) {
        synchronized (mutex) {
            final String key = buildKey(objectToLockId, objectType);
            final ReentrantLock lock = getLock(key);

            if (traceEnable) {
                logger.log(getClass(), TechnicalLogSeverity.TRACE, "tryLock " + lock.hashCode() + " id=" + key);
            }

            if (lock.isHeldByCurrentThread()) {
                return null;
            }
            try {
                if (lock.tryLock(timeout, timeUnit)) {
                    if (traceEnable) {
                        logger.log(getClass(), TechnicalLogSeverity.TRACE, "locked " + lock.hashCode() + " id=" + key);
                    }
                    return new BonitaLock(lock, objectType, objectToLockId);
                }
            } catch (InterruptedException e) {
                logger.log(getClass(), TechnicalLogSeverity.ERROR, "The trylock was interrupted " + lock.hashCode() + " id=" + key);
            }
            if (traceEnable) {
                logger.log(getClass(), TechnicalLogSeverity.TRACE, "not locked " + lock.hashCode() + " id=" + key);
            }
            return null;
        }
    }

    @Override
    public BonitaLock lock(final long objectToLockId, final String objectType) throws SLockException {
        final String key;
        final ReentrantLock lock;
        synchronized (mutex) {
            key = buildKey(objectToLockId, objectType);
            lock = getLock(key);
            if (lock.isHeldByCurrentThread()) {
                throw new SLockException("lock is own by current Thread: " + lock.hashCode() + " id=" + key);
            }
        }
        final long before = System.currentTimeMillis();
        if (traceEnable) {
            logger.log(getClass(), TechnicalLogSeverity.TRACE, "lock " + lock.hashCode() + " id=" + key);
        }
        // outside mutex because it's a long lock
        try {
            boolean tryLock = lock.tryLock(lockTimeout, TimeUnit.SECONDS);
            if (!tryLock) {
                throw new SLockException("Timeout trying to lock " + objectToLockId + ":" + objectType);
            }
        } catch (InterruptedException e) {
            throw new SLockException(e);
        }

        if (traceEnable) {
            logger.log(getClass(), TechnicalLogSeverity.TRACE, "locked " + lock.hashCode() + " id=" + key);
        }
        final long time = System.currentTimeMillis() - before;

        final TechnicalLogSeverity severity = selectSeverity(time);
        if (severity != null) {
            logger.log(getClass(), severity, "The bocking call to lock for the key " + key + " took " + time + "ms.");
            if (TechnicalLogSeverity.DEBUG.equals(severity)) {
                logger.log(getClass(), severity, new Exception("Stack trace : lock for the key " + key));
            }
        }
        return new BonitaLock(lock, objectType, objectToLockId);
    }

    TechnicalLogSeverity selectSeverity(final long time) {
        if (time > 150) {
            return TechnicalLogSeverity.INFO;
        } else if (time > 50) {
            return TechnicalLogSeverity.DEBUG;
        } else {
            // No need to log anything
            return null;
        }
    }
}