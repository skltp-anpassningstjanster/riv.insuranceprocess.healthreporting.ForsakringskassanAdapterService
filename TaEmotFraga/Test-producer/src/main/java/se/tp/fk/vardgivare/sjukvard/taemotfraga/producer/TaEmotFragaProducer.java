/**
 * Copyright (c) 2012, Sjukvardsradgivningen. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package se.tp.fk.vardgivare.sjukvard.taemotfraga.producer;

import java.net.URL;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;

public class TaEmotFragaProducer {
    protected TaEmotFragaProducer() throws Exception {
        System.out.println("Starting Producer");

        // Loads a cxf configuration file to use
        SpringBusFactory bf = new SpringBusFactory();
        URL busFile = this.getClass().getClassLoader().getResource("cxf-producer.xml");
        Bus bus = bf.createBus(busFile.toString());
        SpringBusFactory.setDefaultBus(bus);

        Object implementor = new TaEmotFragaImpl();
        String address = "https://localhost:19000/fk/TaEmotFraga/1/rivtabp20";
        Endpoint.publish(address, implementor);
    }

	public static void main(String[] args) throws Exception {
        new TaEmotFragaProducer();
        System.out.println("Producer ready...");
        
        Thread.sleep(60 * 60 * 1000);
        System.out.println("Producer exiting");
        System.exit(0);
    }
	
}
