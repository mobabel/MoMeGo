package net.mobabel.model;

import java.util.Enumeration;
//#if polish.api.pdaapi or polish.api.pimapi or polish.api.jsr75
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.Event;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;
//#endif

import net.mobabel.item.ItemContact;
import net.mobabel.item.ItemContactExtend;

public class PIMFunc {

	public PIMFunc(){

	}
	/**
	 * Test if the PIM API is supported.
	 * @return true if the PIM API is supported, false otherwise
	 */
	public static boolean isPimApiSupported() {
		boolean isSupported = true;
		String pimApiVersion =  
			System.getProperty("microedition.pim.version");
		if (pimApiVersion == null) {
			isSupported = false;
		}
		return isSupported;
	}

	public int count = 0;
	public String error = "";
	public String flag = "";
    //#if polish.api.pdaapi or polish.api.pimapi or polish.api.jsr75
	private ContactList contactlist;
	//#endif

	/**
	 * depend on the name, not tel
	 * @return
	 */
	public /*static*/ ItemContact[] readPIM() {
		ItemContact[] itemcontacts = new ItemContact[0];
		try {
		    //#if polish.api.pdaapi or polish.api.pimapi or polish.api.jsr75
			//Vector pim = new Vector(0, 1);
			/**
			 * Contact Lists: 0
			 * Event Lists: 1
			 * To-Do Lists: 2
			 */
			int listType = PIM.CONTACT_LIST + 0; 
			String[] pimLists = PIM.getInstance().listPIMLists(listType);
			//#debug debug
			System.out.println("Contact type(Phone[0]/SIM[1]) list size: "+ pimLists.length);

			//this is Sony test code
			/*			PIM pim = PIM.getInstance();
	        for (int m = 0; m < pimLists.length; m++)
	        {
	        	System.out.println("PIM list " + m + " : " + pimLists[m]);
	            contactlist = (ContactList)pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE, pimLists[m]);
	            Enumeration e = contactlist.items();
	            while (e.hasMoreElements())
	            {
	            	Contact contact = (Contact) e.nextElement();
	            	String arr[] = contact.getStringArray(Contact.NAME, 0);
					//#debug debug
	            	System.out.println("Item: " + arr[0]);
					ItemContact itemcontact = new ItemContact();
					itemcontact.setisModified(contact.isModified());
					String[] name = contact.getStringArray(Contact.NAME, 0);
					itemcontact.setNAME_FORMATTED(formatRightName(name));
					String[] addr = contact.getStringArray(Contact.ADDR, 0);
					itemcontact.setADDR((addr[Contact.ADDR_STREET]+" " 
							+addr[Contact.ADDR_POSTALCODE] + " " + addr[Contact.ADDR_LOCALITY]).trim());
					itemcontact.setADDR_STREET(addr[Contact.ADDR_STREET]);
					itemcontact.setADDR_POSTALCODE(addr[Contact.ADDR_POSTALCODE]);
					itemcontact.setADDR_LOCALITY(addr[Contact.ADDR_LOCALITY]);

					itemcontact.setEMAIL(contact.getString(Contact.EMAIL, 0));
					itemcontact.appendTEL(contact.getString(Contact.TEL, 0));
                	itemcontact.setURL(contact.getString(Contact.URL, 0));
                	itemcontact.setUID(contact.getString(Contact.UID, 0));
                	itemcontact.setNOTE(contact.getString(Contact.NOTE, 0));
                	itemcontact.setNICKNAME(contact.getString(Contact.NICKNAME, 0));
                	itemcontact.setORG(contact.getString(Contact.ORG, 0));
                	itemcontact.setTITLE(contact.getString(Contact.TITLE, 0));


					itemcontacts = appendArrayItemContact(itemcontacts, itemcontact);
	            }
	        }*/
			ItemContactExtend itemcontactextend = null;
			for(int i = 0, j = pimLists.length; i < j; i++){
				contactlist = (ContactList)PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE, pimLists[i]);
				//#debug debug
				System.out.println("Contact type element: "+ pimLists[i]);

				PIMList clist = PIM.getInstance().openPIMList(listType, PIM.READ_WRITE, pimLists[i]);
				Enumeration items = clist.items();//can be contactlist.items();
				for (; items.hasMoreElements();) {
					ItemContact itemcontact = new ItemContact();
					PIMItem item = (PIMItem) items.nextElement();
					int fieldCode = 0;
					switch (listType) {
					case PIM.CONTACT_LIST:
						fieldCode = Contact.FORMATTED_NAME;
						break;
					case PIM.EVENT_LIST:
						fieldCode = Event.SUMMARY;
						break;
					case PIM.TODO_LIST:
						fieldCode = ToDo.SUMMARY;
						break;
					}
					Contact contact = (Contact) item;

					itemcontact.setisModified(item.isModified());
					String label = getDisplayedField(contactlist, item, listType);
					//this is for sony ericsson
					if (label == null) {
						label = "";
						String[] name = contact.getStringArray(Contact.NAME, 0);
						label = formatRightName(name);
					}
					/*}else{*/
					//#debug debug
					System.out.println("DisplayedField label-i:" + label+"-"+i);
					itemcontact.setNAME_FORMATTED(label);
					//begin to load the detail info for this person
					int[] fields = contactlist.getSupportedFields();//old is item.get......
					//#debug debug
					//System.out.println("SupportedFields size:" + fields.length);
					int extendminvalue = Contact.EXTENDED_FIELD_MIN_VALUE;
					//#debug debug
					//System.out.println("extendminvalue:" + extendminvalue);
					for (int ii = 0; ii < fields.length; ii++) {
						int field = fields[ii];
						// #debug debug
						//System.out.println("SupportedField code:" + field);
						//exclude CLASS field
						if (isClassField(field, item)) {
							continue;
						}
						//will skip the empty record to save time
						if (item.countValues(field) == 0) {
							// #debug debug
							//System.out.println("filed count values is 0: " + field);
							continue;
						}
						int dataType = item.getPIMList().getFieldDataType(field);
						String fieldlabel = item.getPIMList().getFieldLabel(field);
						switch (dataType) {
						case PIMItem.STRING: {
							//this value could have other formats
							String sValue = item.getString(field, 0);//Contact.ATTR_NONE
							if (sValue == null) {
								sValue = "";
							}
							// #debug debug
							//System.out.println("PIMItem.STRING: "+sValue);
							// cater for specific field styles
							if (item instanceof Contact) {
								switch (field) {
								case Contact.EMAIL:
									int emailCount = contact.countValues(Contact.EMAIL);
									for (int a = 0; a < emailCount; a++){
										int emailAttributes = contact.getAttributes(Contact.EMAIL, a);
										String email = contact.getString(Contact.EMAIL, a);
										if(emailAttributes != Contact.ATTR_NONE){
											if (contactlist.isSupportedAttribute(Contact.EMAIL, emailAttributes)){
												/*if(emailAttributes == Contact.ATTR_HOME){
													itemcontactextend = new ItemContactExtend("EMAIL_HOME", email, emailAttributes);
												}else if(emailAttributes == Contact.ATTR_WORK){
													itemcontactextend = new ItemContactExtend("EMAIL_WORK", email, emailAttributes);
												}else{*/
													itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(emailAttributes), email, emailAttributes);
												/*}*/
												itemcontact.addItemContactExtend(itemcontactextend);
											}else{
												//#debug debug
												System.out.println("not supported label: "+email + " code: "+emailAttributes);
												itemcontactextend = new ItemContactExtend("Unbekannt", email, emailAttributes);	
												itemcontact.addItemContactExtend(itemcontactextend);
											}
										}
									}
									itemcontact.setEMAIL(sValue);
									break;
								case Contact.TEL:
									int telCount = contact.countValues(Contact.TEL);
									//#debug debug
									System.out.println("tel sum count: " + telCount);
									for (int a = 0; a < telCount; a++){
										int telAttributes = contact.getAttributes(Contact.TEL, a);
										String telNumber = contact.getString(Contact.TEL, a);
										
										// check if ATTR_MOBILE is supported, if is mobile, set it at the first place
									/*if (contactlist.isSupportedAttribute(Contact.TEL,Contact.ATTR_MOBILE)){
						                if ((telAttributes & Contact.ATTR_MOBILE) != 0){
						                	itemcontact.insertTELAt(telNumber, 0);
						                }
						                else{
						                	itemcontact.appendTEL(telNumber);
						                }
						              }*/
										//strange, video call is also Contact.ATTR_NONE
										//if(telAttributes != Contact.ATTR_NONE){
											if (contactlist.isSupportedAttribute(Contact.TEL, telAttributes)){
												//#debug debug
												System.out.println("label: " + contact.getPIMList().getAttributeLabel(telAttributes));
												/*if(telAttributes == Contact.ATTR_MOBILE){
													itemcontactextend = new ItemContactExtend("MOBILENUMMER", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_HOME){
													itemcontactextend = new ItemContactExtend("HOMENUMBER", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_WORK){
													itemcontactextend = new ItemContactExtend("OFFICENUMBER", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_FAX){
													itemcontactextend = new ItemContactExtend("FAXNUMBER", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_PAGER){
													itemcontactextend = new ItemContactExtend("PAGERNUMBER", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_MOBILE + Contact.ATTR_HOME){
													itemcontactextend = new ItemContactExtend("MOBILENUMBER_HOME", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_MOBILE + Contact.ATTR_WORK){
													itemcontactextend = new ItemContactExtend("MOBILENUMBER_WORK", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_FAX + Contact.ATTR_HOME){
													itemcontactextend = new ItemContactExtend("FAXNUMBER_HOME", telNumber, telAttributes);
												}else if(telAttributes == Contact.ATTR_FAX + Contact.ATTR_WORK){
													itemcontactextend = new ItemContactExtend("FAXNUMBER_WORK", telNumber, telAttributes);
												}else{*/
													itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(telAttributes), telNumber, telAttributes);
												/*}*/
												if(!telNumber.equals(sValue) )//delete the mobile
													itemcontact.addItemContactExtend(itemcontactextend);
											}else{
												//#debug debug
												System.out.println("not supported label: "+telNumber + " code: "+telAttributes);
												itemcontactextend = new ItemContactExtend("Unbekannt", telNumber, telAttributes);	
												if(!telNumber.equals(sValue) )//delete the mobile
													itemcontact.addItemContactExtend(itemcontactextend);
											}
											
										//}
										
									}
									//General phone number, Contact.ATTR_NONE
									itemcontact.setGeneralPhone(sValue);
									break;
								case Contact.URL:
									int urlCount = contact.countValues(Contact.URL);
									for (int a = 0; a < urlCount; a++){
										int urlAttributes = contact.getAttributes(Contact.URL, a);
										String url = contact.getString(Contact.URL, a);
										if(urlAttributes != Contact.ATTR_NONE){
											if (contactlist.isSupportedAttribute(Contact.URL, urlAttributes)){
												/*if(urlAttributes == Contact.ATTR_HOME){
													itemcontactextend = new ItemContactExtend("URL_HOME", url, urlAttributes);
												}else if(urlAttributes == Contact.ATTR_WORK){
													itemcontactextend = new ItemContactExtend("URL_WORK", url, urlAttributes);
												}else{*/
													itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(urlAttributes), url, urlAttributes);
												/*}*/
												itemcontact.addItemContactExtend(itemcontactextend);
											}else{
												//#debug debug
												System.out.println("not supported label: "+url + " code: "+urlAttributes);
												itemcontactextend = new ItemContactExtend("Unbekannt", url, urlAttributes);	
												itemcontact.addItemContactExtend(itemcontactextend);
											}
										}
									}
									
									itemcontact.setURL(sValue);
									break;
								case Contact.UID:
									itemcontact.setUID(sValue);
									break;           
								case Contact.FORMATTED_NAME:
									//make sure the name is not empty
									if(itemcontact.getNAME_FORMATTED().equals(""))
										itemcontact.setNAME_FORMATTED(sValue);
									break;
								case Contact.NOTE:
									itemcontact.setNOTE(sValue);
									break;
								case Contact.NICKNAME:
									itemcontact.setNICKNAME(sValue);
									break;
								case Contact.ORG:
									itemcontact.setORG(sValue);
									break;
								case Contact.TITLE:
									itemcontact.setTITLE(sValue);
									break;
								case Contact.PHOTO_URL:
									itemcontact.setPHOTO_URL(sValue);
									break;
								case Contact.PUBLIC_KEY_STRING:
									itemcontact.setPUBLIC_KEY_STRING(sValue);
									break;
								default:
									//#debug debug
									System.out.println("this is extended In String:" + field);
								}
							}	
							break;
						}
						case PIMItem.BOOLEAN: {
							//formItem = new StringItem(fieldlabel,item.getBoolean(field, 0) ? "yes" : "no");
							break;
						}
						case PIMItem.STRING_ARRAY: {
							String[] a = item.getStringArray(field, 0);//Contact.ATTR_NONE, could be Contact.ATTR_HOME or Contact.ATTR_WORK
							if (a != null) {
								if (item instanceof Contact) {
									//#debug debug
									System.out.println("fieldlabel/joinStringArray:" + fieldlabel + "/" + joinStringArray(a));
									switch (field) {
									case Contact.ADDR:
										int addrCount = contact.countValues(Contact.ADDR);
										for (int b = 0; b < addrCount; b++){
											int addrAttributes = contact.getAttributes(Contact.ADDR, b);
											String[] adds = contact.getStringArray(Contact.ADDR, b);
											if(addrAttributes != Contact.ATTR_NONE){
												if(addrAttributes == Contact.ATTR_WORK){
													try{
													/*if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_STREET)){
														itemcontactextend = new ItemContactExtend("ADDRESS_STREET_BUSSINESS", adds[Contact.ADDR_STREET], Contact.ADDR_STREET);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_POSTALCODE)){
														itemcontactextend = new ItemContactExtend("ADDRESS_POSTALCODE_BUSSINESS", adds[Contact.ADDR_POSTALCODE], Contact.ADDR_POSTALCODE);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_LOCALITY)){
														itemcontactextend = new ItemContactExtend("ADDRESS_LOCALITY_BUSSINESS", adds[Contact.ADDR_LOCALITY], Contact.ADDR_LOCALITY);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_EXTRA)){
														itemcontactextend = new ItemContactExtend("ADDRESS_EXTRA_BUSSINESS", adds[Contact.ADDR_EXTRA], Contact.ADDR_EXTRA);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_POBOX)){
														itemcontactextend = new ItemContactExtend("ADDRESS_POBOX_BUSSINESS", adds[Contact.ADDR_POBOX], Contact.ADDR_POBOX);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_REGION)){
														itemcontactextend = new ItemContactExtend("ADDRESS_REGION_BUSSINESS", adds[Contact.ADDR_REGION], Contact.ADDR_REGION);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_COUNTRY)){
														itemcontactextend = new ItemContactExtend("ADDRESS_COUNTRY_BUSSINESS", adds[Contact.ADDR_COUNTRY], Contact.ADDR_COUNTRY);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else*/ if (contactlist.isSupportedArrayElement(Contact.ADDR, addrAttributes)){
														itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(addrAttributes), adds[addrAttributes], addrAttributes);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else{
														//#debug debug
														System.out.println("not supported label: "+adds[addrAttributes] + " code: "+addrAttributes);
														itemcontactextend = new ItemContactExtend("Unbekannt", adds[addrAttributes], addrAttributes);	
														itemcontact.addItemContactExtend(itemcontactextend);
													}
													}catch(Exception e){
														//#debug error
														System.out.println("Err: " + e.getMessage());
													}
												}
												else if(addrAttributes == Contact.ATTR_HOME){
													try{
													/*if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_STREET)){
														itemcontactextend = new ItemContactExtend("ADDRESS_STREET_HOME", adds[Contact.ADDR_STREET], Contact.ADDR_STREET);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_POSTALCODE)){
														itemcontactextend = new ItemContactExtend("ADDRESS_POSTALCODE_HOME", adds[Contact.ADDR_POSTALCODE], Contact.ADDR_POSTALCODE);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_LOCALITY)){
														itemcontactextend = new ItemContactExtend("ADDRESS_LOCALITY_HOME", adds[Contact.ADDR_LOCALITY], Contact.ADDR_LOCALITY);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_EXTRA)){
														itemcontactextend = new ItemContactExtend("ADDRESS_EXTRA_HOME", adds[Contact.ADDR_EXTRA], Contact.ADDR_EXTRA);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_POBOX)){
														itemcontactextend = new ItemContactExtend("ADDRESS_POBOX_HOME", adds[Contact.ADDR_POBOX], Contact.ADDR_POBOX);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_REGION)){
														itemcontactextend = new ItemContactExtend("ADDRESS_REGION_HOME", adds[Contact.ADDR_REGION], Contact.ADDR_REGION);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_COUNTRY)){
														itemcontactextend = new ItemContactExtend("ADDRESS_COUNTRY_HOME", adds[Contact.ADDR_COUNTRY], Contact.ADDR_COUNTRY);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else*/ if (contactlist.isSupportedArrayElement(Contact.ADDR, addrAttributes)){
														itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(addrAttributes), adds[addrAttributes], addrAttributes);
														itemcontact.addItemContactExtend(itemcontactextend);
													}else{
														//#debug debug
														System.out.println("not supported label: "+adds[addrAttributes] + " code: "+addrAttributes);
														itemcontactextend = new ItemContactExtend("Unbekannt", adds[addrAttributes], addrAttributes);	
														itemcontact.addItemContactExtend(itemcontactextend);
													}
													}catch(Exception e){
														//#debug error
														System.out.println("Err: " + e.getMessage());
													}
												}
											}
										}		
										itemcontact.setADDR(joinStringArray(a));
										if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_STREET))
											itemcontact.setADDR_STREET(a[Contact.ADDR_STREET]);
										else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_POSTALCODE))
											itemcontact.setADDR_POSTALCODE(a[Contact.ADDR_POSTALCODE]);
										else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_LOCALITY))
											itemcontact.setADDR_LOCALITY(a[Contact.ADDR_LOCALITY]);
										else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_EXTRA)){
											itemcontactextend = new ItemContactExtend(/*"ADDRESS_EXTRA"*/contact.getPIMList().getAttributeLabel(Contact.ADDR_EXTRA), a[Contact.ADDR_EXTRA], Contact.ADDR_EXTRA);
											itemcontact.addItemContactExtend(itemcontactextend);
										}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_POBOX)){
											itemcontactextend = new ItemContactExtend(/*"ADDRESS_POBOX"*/contact.getPIMList().getAttributeLabel(Contact.ADDR_POBOX), a[Contact.ADDR_POBOX], Contact.ADDR_POBOX);
											itemcontact.addItemContactExtend(itemcontactextend);
										}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_REGION)){
											itemcontactextend = new ItemContactExtend(/*"ADDRESS_REGION"*/contact.getPIMList().getAttributeLabel(Contact.ADDR_REGION), a[Contact.ADDR_REGION], Contact.ADDR_REGION);
											itemcontact.addItemContactExtend(itemcontactextend);
										}else if (contactlist.isSupportedArrayElement(Contact.ADDR,Contact.ADDR_COUNTRY)){
											itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(Contact.ADDR_COUNTRY)/*"ADDRESS_COUNTRY"*/, a[Contact.ADDR_COUNTRY], Contact.ADDR_COUNTRY);
											itemcontact.addItemContactExtend(itemcontactextend);
										}
										break;
									case Contact.NAME:
										//make sure the name is not empty
										itemcontact.setNAME(joinStringArray(a));
										if(itemcontact.getNAME().equals("")){
											itemcontact.setNAME(itemcontact.getNAME_FORMATTED());
										}
										if(itemcontact.getNAME_FORMATTED().equals("")){
											itemcontact.setNAME_FORMATTED(joinStringArray(a));
										}
										if(a[Contact.NAME_GIVEN]!=null && !a[Contact.NAME_GIVEN].equals("null")
												&& !a[Contact.NAME_GIVEN].equals("")){
											itemcontact.setNAME_GIVEN(a[Contact.NAME_GIVEN]);					                    
										}else{
											itemcontact.setNAME_GIVEN(getRightNames(itemcontact.getNAME())[0]);
										}
										if(a[Contact.NAME_FAMILY]!=null && !a[Contact.NAME_FAMILY].equals("null")
												&&!a[Contact.NAME_FAMILY].equals("")){
											itemcontact.setNAME_FAMILY(a[Contact.NAME_FAMILY]);
										}else{
											itemcontact.setNAME_FAMILY(getRightNames(itemcontact.getNAME())[1]);
										}

										break;
									default:
										//#debug debug
										System.out.println("this is extended in STRING_ARRAY:" + field);
									}

									for (int iii = 0; iii < a.length; iii++) {
										//item.getPIMList().isSupportedArrayElement(Contact.ADDR, Contact.ADDR_STREET);
										//String elementLabel = item.getPIMList().getArrayElementLabel(field, iii);
										// #debug debug
										//System.out.println("element type/Label: " + elementtype+"/"+elementLabel);
									}
								}
							}
							break;
						}
						case PIMItem.DATE: {
							long time = item.getDate(field, 0);//Contact.ATTR_NONE
							//some fields are date only, without a time.
							if (item instanceof Contact) {
								switch (field) {
								case Contact.BIRTHDAY:
									/*int dateCount = contact.countValues(Contact.BIRTHDAY);
									for (int a = 0; a < dateCount; a++){
										int dateAttributes = contact.getAttributes(Contact.BIRTHDAY, a);
										long date = contact.getDate(Contact.BIRTHDAY, a);
										if(dateAttributes != Contact.ATTR_NONE){
											if (contactlist.isSupportedAttribute(Contact.TEL, dateAttributes)){
												//#debug debug
												System.out.println("label: " + contact.getPIMList().getAttributeLabel(dateAttributes));
												itemcontactextend = new ItemContactExtend(contact.getPIMList().getAttributeLabel(dateAttributes), date+"", dateAttributes);
												itemcontact.addItemContactExtend(itemcontactextend);
											}else{
												//#debug debug
												System.out.println("not supported label: "+date + " code: "+dateAttributes);
												itemcontactextend = new ItemContactExtend("Unbekannt", date+"", dateAttributes);	
												itemcontact.addItemContactExtend(itemcontactextend);
											}
										}
									}*/
									itemcontact.setBIRTHDAY(time+"");
									break;
								case Contact.REVISION:
									itemcontact.setBIRTHDAY(time+"");
									break;
								default:
									//#debug debug
									System.out.println("this is extended in date:" + field);
								
								}
							}
							break;
						}
						case PIMItem.INT: {
							//formItem = new TextField(fieldlabel,String.valueOf(item.getInt(field, 0)),
							//64, TextField.DECIMAL);
							//This will get the unique UID of pim record, 
							if(itemcontact.getUID().equals(""))
								itemcontact.setUID(String.valueOf(item.getInt(field, 0)));
							break;
						}
						case PIMItem.BINARY: {
							byte[] data = item.getBinary(field, 0);//Contact.ATTR_NONE
							if (data != null) {
								//#debug debug
								System.out.println("BINARY data length: " + data.length);
								if (item instanceof Contact) {
									switch (field) {
									case Contact.PHOTO:
										itemcontact.setPHOTO(data);
										break;
									case Contact.PUBLIC_KEY:
										itemcontact.setPUBLIC_KEY(data);
										break;
									default:
										//#debug debug
										System.out.println("this is extended in binary:" + field);
									}
								}
							}
							break;
						}
						}
						//if (formItem != null) { 
						//fieldTable.put(formItem, new Integer(field));
						//}
					}
					//===================
					//}
					//check the familyname and givenname again
					if(itemcontact.getNAME_GIVEN().equals("")){
						itemcontact.setNAME_GIVEN(getRightNames(itemcontact.getNAME_FORMATTED())[0]);					                    
					}
					if(itemcontact.getNAME_FAMILY().equals("")){
						itemcontact.setNAME_FAMILY(getRightNames(itemcontact.getNAME_FORMATTED())[1]);
					}
					//if names have been filled, use it as formated name
					if(!itemcontact.getNAME_GIVEN().equals("") && !itemcontact.getNAME_FAMILY().equals("")){
						if(!itemcontact.getNAME_GIVEN().equals(itemcontact.getNAME_FAMILY()))
							itemcontact.setNAME_FORMATTED(itemcontact.getNAME_GIVEN()+" "+itemcontact.getNAME_FAMILY());
						else
							itemcontact.setNAME_FORMATTED(itemcontact.getNAME_FAMILY());
					}
					//check the address, postcode and city


					//if name is empty, dont add it into array
					if(!itemcontact.getNAME_FORMATTED().equals("")){
						itemcontacts = appendArrayItemContact(itemcontacts, itemcontact);
					}
					count++;
				}

			}	
			//#endif
			return itemcontacts;
		} 
		catch (Exception e) {
			//#debug error
			System.out.println("Failed to read PIM: " + e.getMessage());
			error = e.getMessage();
			return itemcontacts;
		}
	    //#if polish.api.pdaapi or polish.api.pimapi or polish.api.jsr75
		finally{
			try {
				if(contactlist!=null)
					contactlist.close();
			} catch (PIMException e) {
			}
		}
		//#endif
	}
	
    //#if polish.api.pdaapi or polish.api.pimapi or polish.api.jsr75
	static String formatRightName(String[] names){
		StringBuffer buffer = new StringBuffer();
		if(names[Contact.NAME_GIVEN]!=null && !names[Contact.NAME_GIVEN].equals("null")){
			buffer.append(names[Contact.NAME_GIVEN]);
		}
		if(names[Contact.NAME_FAMILY]!=null && !names[Contact.NAME_FAMILY].equals("null")){
			buffer.append(" "+ names[Contact.NAME_FAMILY]);
		}

		return buffer.toString().trim();
	}

	/**
	 * names[0] is given name, first name
	 * names[1] is family name, name
	 */
	static String[] getRightNames(String fullname){
		fullname = fullname.trim();
		String[] names = new String[2];
		if(fullname.indexOf(",")<0){
			if(fullname.indexOf(" ")<0){
				names[0] = fullname;
				names[1] = fullname;
			}else{
				String[]  temp = CommonFunc.split(fullname, " ");
				names[0] = temp[0].trim();
				names[1] = temp[temp.length-1].trim();
			}
		}else{
			String[]  temp = CommonFunc.split(fullname, ",");
			names[0] = temp[0].trim();
			names[1] = temp[temp.length-1].trim();
		}

		return names;
	}

	static int getDisplayedFieldCode(int listType) {
		int fieldCode = 0;
		switch (listType) {
		case PIM.CONTACT_LIST:
			fieldCode = Contact.FORMATTED_NAME;
			break;
		case PIM.EVENT_LIST:
			fieldCode = Event.SUMMARY;
			break;
		case PIM.TODO_LIST:
			fieldCode = ToDo.SUMMARY;
			break;
		}
		return fieldCode;
	}

	static PIMItem fixDisplayedField(ContactList contactlist, PIMItem item, int listType) {
		int fieldCode = getDisplayedFieldCode(listType);
		if (listType == PIM.CONTACT_LIST) {
			boolean defined = false;
			if(!contactlist.isSupportedField(Contact.FORMATTED_NAME)){
				return item;
			}
			if (item.countValues(fieldCode) != 0) {
				String s = item.getString(fieldCode, 0);
				if (s != null && s.trim().length() > 0) {
					defined = true;
				}
			}
			if (!defined) {
				// try to fill in the values from NAME
				if (item.countValues(Contact.NAME) != 0) {
					String[] a = item.getStringArray(Contact.NAME, 0);
					if (a != null) {
						StringBuffer sb = new StringBuffer();
						if (a[Contact.NAME_GIVEN] != null) {
							sb.append(a[Contact.NAME_GIVEN]);
						}
						if (a[Contact.NAME_FAMILY] != null) {
							if (sb.length() > 0) {
								sb.append(" ");
							}
							sb.append(a[Contact.NAME_FAMILY]);
						}
						String s = sb.toString().trim();
						if (s.length() > 0) {
							if (item.countValues(fieldCode) == 0) {
								item.addString(fieldCode, Contact.ATTR_NONE, s);
							} else {
								item.setString(fieldCode, 0, Contact.ATTR_NONE, s);
							}
						}
					}
				}
			}
		}
		return item;
	}

	static String getDisplayedField(ContactList contactlist, PIMItem item, int listType) {
		int fieldCode = getDisplayedFieldCode(listType);
		//sony ericsson does not support Contact.FORMATTED_NAME
		if(!contactlist.isSupportedField(Contact.FORMATTED_NAME)){
			return null;
		}
		item = fixDisplayedField(contactlist, item, listType);
		String fieldValue = null;
		if (item.countValues(fieldCode) != 0) {
			String s = item.getString(fieldCode, 0);
			if (s != null && s.trim().length() != 0) {
				fieldValue = s;
			}
		}
		return fieldValue;
	}

	public static boolean isClassField(int field, PIMItem item) {
		return item instanceof Contact && field == Contact.CLASS
		|| item instanceof Event && field == Event.CLASS
		|| item instanceof ToDo && field == ToDo.CLASS;
	}

	public static String joinStringArray(String[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null && a[i].length() > 0) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(a[i]);
			}
		}
		return sb.toString();
	}
	//#endif

	public static ItemContact[] setElementItemContactAt(ItemContact[] oldarray, ItemContact newarray, int number) throws Exception{
		ItemContact[] last = oldarray;
		if(number > oldarray.length-1)
			throw new Exception("IndexOutOfBoundary");
		try{
			last[number] = newarray;
			return last;
		}catch(Exception e){
			//#debug error
			System.out.println("E in setItemContactElementAt: "+e.getMessage());
			return oldarray;
		}finally{
			oldarray = null;
		}
	}

	public static ItemContact[] insertElementItemContactAt(ItemContact[] oldarray, ItemContact newarray, int number) throws Exception{
		ItemContact[] last = oldarray;
		if(number > oldarray.length-1)
			throw new Exception("IndexOutOfBoundary");
		try{
			last = new ItemContact[oldarray.length+1];
			System.arraycopy(oldarray, 0, last, 0, number);
			System.arraycopy(oldarray, number+1, last, number+1, oldarray.length-number);
			last[number] = newarray;
			return last;
		}catch(Exception e){
			//#debug error
			System.out.println("E in insertElementItemContactAt: "+e.getMessage());
			return oldarray;
		}finally{
			oldarray = null;
		}
	}

	public static ItemContact[] removeElementItemContactAt(ItemContact[] oldarray, int number) throws Exception{
		ItemContact[] last = oldarray;
		if(number > oldarray.length-1)
			throw new Exception("IndexOutOfBoundary");
		try{
			last = new ItemContact[oldarray.length-1];
			System.arraycopy(oldarray, 0, last, 0, number);
			System.arraycopy(oldarray, number+1, last, number, oldarray.length-number-1);
			return last;
		}catch(Exception e){
			//#debug error
			System.out.println("E in removeElementItemContactAt: "+e.getMessage());
			return oldarray;
		}finally{
			oldarray = null;
		}
	}

	/**
	 * 
	 * @param String[] oldarray
	 * @param String newelement
	 * @return
	 */
	public static ItemContact[] appendArrayItemContact(ItemContact[] oldarray, ItemContact newelement){
		try{
			ItemContact[] newarray = {newelement};
			return appendArrayItemContact(oldarray, newarray);
		}catch(Exception e){
			//#debug error
			System.out.println("E appendArrayItemContact: "+e.getMessage());
			return oldarray;
		}
	}

	/**
	 * 
	 * @param String[] oldarray
	 * @param String[] newarray
	 * @return
	 */
	public static ItemContact[] appendArrayItemContact(ItemContact[] oldarray, ItemContact[] newarray){
		ItemContact[] last = oldarray;
		try{
			if(newarray == null || oldarray == null){
				return oldarray;
			}
			int oldlen = oldarray.length;
			int newlen = newarray.length;
			if(newlen == 0)
				return oldarray;

			last = new ItemContact[oldlen + newlen];
			System.arraycopy(oldarray, 0, last, 0, oldlen);
			System.arraycopy(newarray, 0, last, oldlen, newlen);

		}catch(Exception e){
			//#debug error
			System.out.println("E appendArrayItemContact: "+e.getMessage());
			return oldarray;
		}  
		oldarray = null;
		newarray = null;
		return last;
	}

	public static ItemContact[] getNewItemContacts(ItemContact[] itemcontacts){
		ItemContact[] last = new ItemContact[0];
		try{
			int count  = itemcontacts.length;
			for(int i=0; i< count; i++){
				if(!itemcontacts[i].gethasBackuped()){
					last =  appendArrayItemContact(last, itemcontacts[i]);
				}
			}
			return last;
		}catch(Exception e){
			//#debug error
			System.out.println("E getNewItemContacts: "+e.getMessage());
			return last;
		}
	}

	public static ItemContact[] setItemContactStatus4Backuped(ItemContact[] itemcontacts){
		try{
			int count  = itemcontacts.length;
			for(int i=0; i< count; i++){
				itemcontacts[i].sethasBackuped(true);
			}
			return itemcontacts;
		}catch(Exception e){
			//#debug error
			System.out.println("E setItemContactStatus4Backuped: "+e.getMessage());
			return itemcontacts;
		}
	}
}
