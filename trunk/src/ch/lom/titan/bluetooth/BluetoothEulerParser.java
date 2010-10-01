/*
 * Copyright (C) 2010 Clemens Lombriser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ch.lom.titan.bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import ch.lom.titan.visual.opengl.EulerAngle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothEulerParser extends Thread {

	private BluetoothDevice m_device;
	private EulerAngle      m_angles;
	private boolean m_finish = false;
	private boolean m_connected = false;
	
	BluetoothEulerParser(BluetoothDevice device, EulerAngle angles) {
		m_device = device;
		m_angles = angles;
	}
	
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final int AVAILBUFFERLENGTH = 128;
	
	public void run() {
	
		setName("BluetoothConnection"+m_device.getAddress());
		
		try {
			// just to make sure (as recommended by the documentation)
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			
			BluetoothSocket bs = m_device.createRfcommSocketToServiceRecord(SPP_UUID);
			
			bs.connect();
			m_connected = true;
			
			InputStream is = bs.getInputStream();
			DataInputStream in = new DataInputStream(is); 
			
			byte [] availableBuffer = new byte[AVAILBUFFERLENGTH];
			
			while(!m_finish) {
				if ( EulerAngle.parseLine(in.readLine(),m_angles) == null ) {
					// if the line could not be read, try again
				} else {
					// line successfully read - clear up buffer to make sure no lag is generated
					int availableBytes = in.available();
					in.read(availableBuffer, 0, availableBytes>AVAILBUFFERLENGTH?AVAILBUFFERLENGTH:availableBytes);
				}
			}
			
			in.close();
			is.close();
			bs.close();
			
		} catch (IOException e) {
			//BodyActivity.singleton.setText("Lost connection to " + m_device.getName());
		} finally {
			m_connected = false;
		}
	}
	
	public boolean isConnected() {
		return m_connected;
	}
	
	public void requestExitAndWait() {
		m_finish = true;
		try{
			join(5000);
		} catch(InterruptedException e) {
		}
	}	
	
	public String getDeviceName() {
		return m_device.getName() + " (" + m_device.getAddress() + ")";
	}
	
}
