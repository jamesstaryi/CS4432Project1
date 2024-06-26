//CLASS FOR FRAMES
import java.io.*;
public class frame {
    //holds the file conent
    private byte[] content = new byte[4000];

    //true if content of this block has changed
    private boolean dirty = false;

    //true if this block has been pinned
    private boolean pinned;

    //id of the block stored in frame
    private int blockID;

    //initializes the frame
    public void initialize(){
        this.dirty = false;
        this.pinned = false;
        this.blockID = -1;
    }

    //gets the record of 40 bytes from given recordNumber
    public String getRecord(int recordNumber){
        int startPosition = (recordNumber-1) * 40;
        byte[] recordBytes = new byte[40];
        System.arraycopy(content, startPosition, recordBytes, 0, 40);
        return new String(recordBytes);
    }

    //updates he record of 40 bytes from given recordNumber with newContent
    public void updateRecord(int recordNumber, String newContent){
        int startPosition = (recordNumber-1) * 40;
        byte[] newRecordBytes = newContent.getBytes();
        System.arraycopy(newRecordBytes, 0, content, startPosition, 40);
        dirty = true;
    }

    public boolean getDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean getPinned() {
        return this.pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public int getBlockID() {
        return this.blockID;
    }

    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public byte[] getContent(){
        return content;
    }

    //sets the content with the given fileNumber
    public void setContent(int fileNumber) {
        String fileName = "F" + fileNumber + ".txt";
        try (FileInputStream fileInputStream = new FileInputStream("Project1/" + fileName)) {
            int bytesRead = fileInputStream.read(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //updates the file given the fileNumber with the current content
    public void updateContent(int fileNumber) {
        String fileName = "F" + fileNumber + ".txt";
        try (FileOutputStream fileOutputStream = new FileOutputStream("Project1/" + fileName)) {
            fileOutputStream.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
