import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP {

	private static final int DEFAULTPACKETSIZE = 256;
	private static final int DEFAULTDELAY = 0;

	private static byte[] mp3ToBytes(String path) throws IOException {
		File file = new File(path); // check whether filename has been passed
		FileInputStream fin = new FileInputStream(file);
		int fileByteCount = (int) file.length();
		byte[] fileContents = new byte[fileByteCount];
		fin.read(fileContents, 0, fileByteCount);
		fin.close();
		return fileContents;
	}

	private static void send(byte[] audioByte, int audioByteLength, int packetSize, String host, int port, int delay)
			throws IOException, InterruptedException {
		DatagramSocket ds = new DatagramSocket();
		// ds.bind(new InetSocketAddress(port));

		int bytesLeft = audioByteLength;
		byte[] fragment = new byte[packetSize];
		// System.out.println("ip"+ InetAddress.getByName(host));
		while (bytesLeft >= packetSize) {
			for (int i = 0; i < packetSize; i++)
				fragment[i] = audioByte[audioByteLength - bytesLeft + i];
			ds.send(new DatagramPacket(fragment, packetSize, InetAddress.getByName(host), port));
			bytesLeft -= packetSize;
			Thread.sleep(delay);
		}
		if (bytesLeft > 0) {
			fragment = new byte[bytesLeft];
			for (int i = 0; i < bytesLeft; i++)
				fragment[i] = audioByte[audioByteLength - bytesLeft + i];
			ds.send(new DatagramPacket(fragment, bytesLeft, InetAddress.getByName(host), port));
		}
		ds.close();
	}

	private static boolean areArgumentsValid(String[] args) {
		try {
			if (args.length > 6 || args[0] == null || args[1] == null || args[2] == null)
				return false;
			int portNumber = Integer.parseInt(args[1]);
			if (portNumber > 65535 || portNumber <= 1024)
				return false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			if (!areArgumentsValid(args)) {
				System.out.println("Invalid Arguments");
				System.exit(0);
			}
			byte[] audioByte = mp3ToBytes(args[2]);
			int audioByteLength = audioByte.length;
			int packetSize = (args.length < 4) ? DEFAULTPACKETSIZE : Integer.parseInt(args[3]);
			int delay = (args.length < 5) ? DEFAULTDELAY : Integer.parseInt(args[4]);
			send(audioByte, audioByteLength, packetSize, args[0], Integer.parseInt(args[1]), delay);
			
		} catch (NumberFormatException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}