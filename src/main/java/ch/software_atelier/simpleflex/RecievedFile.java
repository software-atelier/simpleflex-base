package ch.software_atelier.simpleflex;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Represents a receifed File.
 * @author tk
 */
public class RecievedFile extends RecievedData {
    private File _file;

    private String _fileName = "";

    private BufferedOutputStream _fos;

    public RecievedFile() {
        try {
            _file = File.createTempFile("sfRecFile", "tmp");

            _fos = new BufferedOutputStream(
                    new FileOutputStream(_file, true));

        }
        catch (IOException ioe) {
            _successfulReceived = false;
        }
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public String fileName() {
        return _fileName;
    }

    public int type() {
        return this.TYPE_FILE;
    }

    public File file() {
        return _file;
    }

    public void apendToFile(byte[] data) {
        try {
            _fos.write(data);
        }
        catch (IOException ioe) {
            _successfulReceived = false;
        }
    }

    public byte[] getData() {
        return Utils.readFile(_file);
    }

    public boolean successfulReceived() {
        return _successfulReceived;
    }

    public void deleteTmpFile() {
        _file.delete();
    }

    @Override
    public void done() {
        try {
            _fos.flush();
            _fos.close();
        }
        catch (IOException ex) {
        }
    }

    protected void finalize() throws Throwable {
        if (_file.exists())
            if (!_file.delete())
                _file.deleteOnExit();

    }
}
