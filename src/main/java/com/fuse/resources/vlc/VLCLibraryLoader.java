package com.fuse.resources.vlc;

import java.io.File;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibC;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCLibraryLoader {
	private static final String PLUGIN_ENV_NAME = "VLC_PLUGIN_PATH";

	public static void loadLibrary() {
		String libraryPath = System.getProperty("java.library.path");
		if (RuntimeUtil.isMac()) {
			for (String path : libraryPath.split(":")) {
				File vlcPath = new File(path + "/vlc_mac");
				if (vlcPath.exists() && vlcPath.isDirectory()) {
					System.out.println("FOUND VLC MACOSX LIBRARY IN " + vlcPath.getPath());
					String vlclib = vlcPath.getPath() + "/lib";
					NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlclib);
					LibC.INSTANCE.setenv(PLUGIN_ENV_NAME, String.format("%s/../plugins", new Object[]{vlclib}), 1);
				}
			}
		} else if (RuntimeUtil.isWindows()) {
			for (String path : libraryPath.split(";")) {
				File vlcPath = new File(path + "/vlc_win");
				if (vlcPath.exists() && vlcPath.isDirectory()) {
					System.out.println("FOUND VLC WINDOWS64 LIBRARY IN " + vlcPath.getPath());
					String vlclib = vlcPath.getPath();
					NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlclib);
					String pluginPath = String.format("%s\\%s", vlclib, "plugins");
					LibC.INSTANCE._putenv(String.format("%s=%s", PLUGIN_ENV_NAME, pluginPath));
				}
			}
		}
	}
}
