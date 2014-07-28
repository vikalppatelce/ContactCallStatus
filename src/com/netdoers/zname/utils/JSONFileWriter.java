package com.netdoers.zname.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.netdoers.zname.AppConstants;
import com.netdoers.zname.beans.FriendsDTO;

public class JSONFileWriter {

	private static final String TAG = JSONFileWriter.class.getSimpleName();
	@SuppressWarnings("deprecation")
	public static void jsonFriendList(String _id,String zname, String contact_no, String full_name, String imagePath) {
		File jsonFile = new File(getJSONFilePath(), "friendJSON.json");
		try {
			File f = new File(getJSONFilePath()+"/friendJSON.json");
			if(f.exists()){
				readJSONFile(jsonFile, _id,zname, contact_no, full_name, imagePath);
			}else{
				f.createNewFile();
				ArrayList<FriendsDTO> mListFriendsDTO = new ArrayList<FriendsDTO>();
				FriendsDTO friendsDTO = new FriendsDTO();
				friendsDTO.setContactID(_id);
				friendsDTO.setContactNumber(contact_no);
				friendsDTO.setFullName(full_name);
				friendsDTO.setProfilePath(imagePath);
				friendsDTO.setzName(zname);
				mListFriendsDTO.add(friendsDTO);
				writeJSONFile(mListFriendsDTO, f);
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static File getJSONFilePath() {
		File imageDirectory = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		} else {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH_DATA);
		}
		imageDirectory.mkdirs();
		return imageDirectory;
	}

	private static void readJSONFile(File jsonFile, String _id, String zname, String contact_no, String full_name, String imagePath) throws JsonParseException,
			IOException {
		ArrayList<FriendsDTO> mListFriendsDTO = new ArrayList<FriendsDTO>();

		JsonFactory f = new JsonFactory();
		JsonParser jp = null;
		try {
			jp = f.createJsonParser(jsonFile);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FriendsDTO friendsDTO = new FriendsDTO();
//		jp.nextToken(); // will return JsonToken.START_OBJECT (verify?)
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			if ("friends".equals(jp.getCurrentName())) {
				while (jp.nextToken() != JsonToken.END_ARRAY) {
					String couponfield = jp.getCurrentName();

					if ("}".equals(jp.getText())) {
						mListFriendsDTO.add(friendsDTO);
						friendsDTO = new FriendsDTO();
					}
					
					if ("contact_id".equals(couponfield)) {
						friendsDTO.setContactID(jp.getText());
					}else if ("zname".equals(couponfield)) {
						friendsDTO.setzName(jp.getText());
					} else if ("contact_number".equals(couponfield)) {
						friendsDTO.setContactNumber(jp.getText());
					} else if ("profile_pic".equals(couponfield)) {
						friendsDTO.setProfilePath(jp.getText());
					} else if ("full_name".equals(couponfield)) {
						friendsDTO.setFullName(jp.getText());
					}
				}
			}
		}
		
		FriendsDTO appFriendsDTO = new FriendsDTO();
		appFriendsDTO.setContactID(_id);
		appFriendsDTO.setzName(zname);
		appFriendsDTO.setContactNumber(contact_no);
		appFriendsDTO.setFullName(full_name);
		appFriendsDTO.setProfilePath(imagePath);
		mListFriendsDTO.add(appFriendsDTO);
		writeJSONFile(mListFriendsDTO,jsonFile);
			
	}

	private static void writeJSONFile(ArrayList<FriendsDTO> mListFriendsDTO, File jsonFile) {
		JsonFactory f = new JsonFactory();
		JsonParser jp = null;
		JsonGenerator jsonGenerator = null;
		try {
			jsonGenerator = f.createJsonGenerator(jsonFile, JsonEncoding.UTF8);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeFieldName("friends");
			jsonGenerator.writeStartArray();
			
			for (int i = 0; i < mListFriendsDTO.size(); i++) {
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("contact_id", mListFriendsDTO.get(i).getContactID());
				jsonGenerator.writeStringField("zname", mListFriendsDTO.get(i).getzName());
				jsonGenerator.writeStringField("contact_number", mListFriendsDTO.get(i).getContactNumber());
				jsonGenerator.writeStringField("profile_pic", mListFriendsDTO.get(i).getProfilePath());
				jsonGenerator.writeStringField("full_name", mListFriendsDTO.get(i).getFullName());
				jsonGenerator.writeEndObject();
			}
			
			jsonGenerator.writeEndArray();
			jsonGenerator.writeEndObject();
			jsonGenerator.close();
			Log.i(TAG, "Successfully write json file!");
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
