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

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import ch.lom.titan.visual.Body;
import ch.lom.titan.visual.R;

public class AssignBTSensors extends ListActivity {

	ArrayList<HashMap<String,String>> m_list = new ArrayList<HashMap<String,String>>();
	
	SimpleAdapter m_adapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (int i=0; i<Body.ORIENTATION_SENSOR_NAMES.length; i++) {
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("line1", Body.ORIENTATION_SENSOR_NAMES[i]);
			
			if (Body.m_parsers[i] != null) {
				item.put("line2", "Bluetooth: " + Body.m_parsers[i].getDeviceName());
			} else {
				item.put("line2", "unassigned");
			}
			
			
			m_list.add(item);
		}
		
		m_adapter = new SimpleAdapter(this, m_list, R.layout.sensorlistlayout, new String[]{"line1","line2"}, new int[]{R.id.bt_text1, R.id.bt_text2});
		setListAdapter(m_adapter);
	}
	
	protected void onListItemClick(ListView lv, View v, int position, long id) {
		@SuppressWarnings("unchecked")
		//HashMap<String,String> item = (HashMap<String, String>) lv.getSelectedItem();
		HashMap<String,String> item = (HashMap<String, String>)m_adapter.getItem(position);
		
		if ( ! BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Toast msg = Toast.makeText(this, "Bluetooth is not enabled!", Toast.LENGTH_LONG);
			msg.show();
			return;
		}
		
		Intent setint = new Intent(this, DeviceListActivity.class);
		setint.putExtra(DeviceListActivity.EXTRA_DEVICE_NAME, item.get("line1"));
		startActivityForResult(setint, REQUEST_CONNECT_DEVICE);
		
	}
	
	private static final int REQUEST_CONNECT_DEVICE = 1;
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case REQUEST_CONNECT_DEVICE:
				
				if (resultCode == Activity.RESULT_OK) {
					String sensorname = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);
					String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					
					//BluetoothDevice device = bluetoothadapter.getRemoteDevice(address);
					
					for (int i=0; i<m_adapter.getCount(); i++) {
						@SuppressWarnings("unchecked")
						HashMap<String,String> item = (HashMap<String, String>) m_adapter.getItem(i);
						
						String name = item.get("line1");
						
						if(name.compareTo(sensorname) == 0) { 
							BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
							
							if (Body.m_parsers[i] != null) {
								Body.m_parsers[i].requestExitAndWait();
							}
							Body.m_parsers[i] = new BluetoothEulerParser(device,Body.m_orientations[i]);

							// update address entry
							item.remove("line2");
							item.put("line2","Bluetooth: " + Body.m_parsers[i].getDeviceName());

							Body.m_parsers[i].start();
							
							break;
						}
					}
					
				}
				break;
			default:
				break;
		}
		m_adapter.notifyDataSetChanged();
	}
	
}
