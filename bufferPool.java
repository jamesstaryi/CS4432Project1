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
            return buffers[location].getRecord(recordNumber);
        }
        else{
            int emptyFrame = emptyFrame();
            if(emptyFrame != -1){
                buffers[emptyFrame].setBlockID(emptyFrame);
                buffers[emptyFrame].setContent(fileNumber);
                return buffers[emptyFrame].getRecord(recordNumber);
            }
            else{
                for(int i = 0; i < buffers.length; i++){
                    if(!buffers[i].getPinned()){
                        if(!buffers[i].getDirty()){
                            buffers[i].setBlockID(emptyFrame);
                            buffers[i].setContent(fileNumber);
                            return buffers[i].getRecord(recordNumber);
                        }
                    }
                }
            }
        }
        return null;
    }
}