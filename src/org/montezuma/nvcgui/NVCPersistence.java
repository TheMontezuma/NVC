package org.montezuma.nvcgui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class NVCPersistence {
	
	private static final String theNodeName = "/org/montezuma/nvcgui";
	private static final String FROM = "from";
	private static final String TO = "to";
	private static final String LEVEL = "level";
	private static final String FORCE = "force";
	private static final String SUFFIXES = "suffixes";
	
	static private NVCOptions op;
	
	static void install(NVCOptions options)
	{
		op = options;
	}
	
	@SuppressWarnings("unchecked")
	static void read()
	{
		if(op!=null)
		{
			Preferences prefs = Preferences.userRoot().node(theNodeName);
			op.from = prefs.get(FROM, op.from);
			op.to = prefs.get(TO, op.to);
			op.level = prefs.getInt(LEVEL, op.level);
			op.force = prefs.getBoolean(FORCE, op.force);
			byte[] bytes = prefs.getByteArray(SUFFIXES, null);
			if (bytes != null)
			{
				try
				{
					ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
					op.suffixes = (List<String>) objectIn.readObject();
				    List<String> tmp_suffixes = new ArrayList<String>();
					for(String s:op.suffixes)
					{
						tmp_suffixes.add(s.toUpperCase());
					}
					op.suffixes = tmp_suffixes;
				}
				catch(Exception e)
				{
				}
			}
		}
	}
	
	static void write()
	{
		if(op!=null)
		{
			Preferences prefs = Preferences.userRoot().node(theNodeName);
			prefs.put(FROM, op.from);
			prefs.put(TO, op.to);
			prefs.putInt(LEVEL, op.level);
			prefs.putBoolean(FORCE, op.force);
			try
			{
				ByteArrayOutputStream baos = new  ByteArrayOutputStream();
				ObjectOutputStream oout = new ObjectOutputStream(baos);
				oout.writeObject(op.suffixes);
				oout.close();
				prefs.putByteArray(SUFFIXES, baos.toByteArray());
			}
			catch(IOException e)
			{
			}
		}
	}
}
