/*
 *  Copyright (c) 2024 Your Organization Name
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       GMV
 *
 */

package org.upm.inesdata.monitor;

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.MonitorExtension;

public class ConnectorMonitorExtension implements MonitorExtension {

    @Override
    public Monitor getMonitor() {
        return new Slf4jMonitor();
    }
}
