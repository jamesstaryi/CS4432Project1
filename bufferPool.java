import java.io.File;
import java.util.ArrayList;

public class bufferPool{
    private frame[] buffers;
    public void initialize(int slots){
        this.buffers = new frame[slots];
        for(int i = 0; i < buffers.length; i++){
            buffers[i] = new frame();
            buffers[i].initialize();
        }
    }

    public int blockLocation(int blockID){
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i].getBlockID() == (blockID)){
                return i;
            }
        }
        return -1;
    }

    public byte[] getContent(int blockID){
        int x = this.blockLocation(blockID);
        if(x != -1){
            return buffers[x].getContent();
        }
        return null;
    }

    public int emptyFrame(){
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i].getBlockID() == -1){
                return i;
            }
        }
        return -1;
    }

    public String GET(int k){
        int fileNumber = (int)(Math.ceil((double)k / 100) * 100)/100;
        int recordNumber = k % 100;
        int location = blockLocation(fileNumber);
        if(location != -1){
            return buffers[location].getRecord(recordNumber) + "; File " + fileNumber + " already in memory; Located in Frame " + (location+1);
        }
        else{
            int emptyFrame = emptyFrame();
            if(emptyFrame != -1){
                buffers[emptyFrame].setBlockID(fileNumber);
                buffers[emptyFrame].setContent(fileNumber);
                return buffers[emptyFrame].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (emptyFrame+1);
            }
            else{
                for(int i = 0; i < buffers.length; i++){
                    if(!buffers[i].getPinned()){
                        if(!buffers[i].getDirty()){
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setContent(fileNumber);
                            return buffers[i].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame" + (i+1);
                        }
                        else{
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].updateContent(fileNumber);
                            buffers[i].setDirty(false);
                            return buffers[i].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame" + (i+1);
                        }
                    }
                }
                return "The corresponding block " + fileNumber + " cannot be pinned because the memory buffers are full";
            }
        }
    }

    public String SET(int k, String text){
        int fileNumber = (int)(Math.ceil((double)k / 100) * 100)/100;
        int recordNumber = k % 100;
        int location = blockLocation(fileNumber);
        if(location != -1){
            buffers[location].updateRecord(recordNumber, text);
            return "Write was successful; File " + fileNumber + " already in memory; Located in Frame " + (location+1);
        }
        else{
            int emptyFrame = emptyFrame();
            if(emptyFrame != -1){
                buffers[emptyFrame].setBlockID(fileNumber);
                buffers[emptyFrame].setContent(fileNumber);
                buffers[emptyFrame].updateRecord(recordNumber, text);
                return "Write was successful; Brought file " + fileNumber + " from disk; Placed in Frame " + (emptyFrame+1);
            }
            else{
                for(int i = 0; i < buffers.length; i++){
                    if(!buffers[i].getPinned()){
                        if(!buffers[i].getDirty()){
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setContent(fileNumber);
                            buffers[i].updateRecord(recordNumber, text);
                            return "Write was successful; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame" + (i+1);
                        }
                        else{
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].updateContent(fileNumber);
                            buffers[i].updateRecord(recordNumber, text);
                            return "Write was successful; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame" + (i+1);
                        }
                    }
                }
                return "The corresponding block " + fileNumber + " cannot be pinned because the memory buffers are full; Write was unsuccessful";
            }
        }
    }

    public String PIN(int BID){
        int location = blockLocation(BID);
        if(location != -1){
            if(buffers[location].getPinned()){
                return "File " + BID + " pinned in Frame " + (location+1) + "; Already pinned";
            }
            buffers[location].setPinned(true);
            return "File " + BID + " pinned in Frame " + (location+1) + "; Not already pinned";
        }
        int emptyFrame = emptyFrame();
        if(emptyFrame != -1){
            buffers[emptyFrame].setBlockID(BID);
            buffers[emptyFrame].setContent(BID);
            buffers[emptyFrame].setPinned(true);
            return "File " + 3 + " pinned in Frame " + 2 + "; Not already pinned; Evicted file " + 1 + " from Frame " + 2;
        }
        else{
            for(int i = 0; i < buffers.length; i++){
                if(!buffers[i].getPinned()){
                    if(!buffers[i].getDirty()){
                        
                    }
                    else{
                        
                    }
                }
            }
            return "";
        }
    }
}