package main;

import client.Client;
import banner.Banner;

public class SocketApp {

	public static void main(String args[]) {
		
		// Ustawienia: ----------------------------------------------------------------------------
		
		String[] banners = { "Banner 1", "Banner 2" }; 		// nazwy bannerow
		int startPort = 1201; 								// pierwszy port
		
		// Uruchomienie: --------------------------------------------------------------------------
		
		Client client = new Client();
		Banner[] bannerObjects = new Banner[banners.length];
		
		for (int i = 0; i < banners.length; i++) {
			bannerObjects[i] = new Banner(banners[i], startPort + i);
			bannerObjects[i].start();
		}
		
		client.start();
	}
	
}
