//CLASS FOR BUFFER POOLS
public class bufferPool{
    //list of frames
    private frame[] buffers;

    //initalizes a bufferpool of empty frames
    public void initialize(int slots){
        this.buffers = new frame[slots];
        for(int i = 0; i < buffers.length; i++){
            buffers[i] = new frame();
            buffers[i].initialize();
        }
    }

    //Locates the index of where the blockID is, returns -1 if not in the array of frames
    public int blockLocation(int blockID){
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i].getBlockID() == (blockID)){
                return i;
            }
        }
        return -1;
    }

    //retrieves the content of a block inside a frame
    public byte[] getContent(int blockID){
        int x = this.blockLocation(blockID);
        if(x != -1){
            return buffers[x].getContent();
        }
        return null;
    }

    //returns the first instance of an empty frame in the array. returns -1 if no empty frames available
    public int emptyFrame(){
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i].getBlockID() == -1){
                return i;
            }
        }
        return -1;
    }

    //GET method
    public String GET(int k){
        int fileNumber = (int)(Math.ceil((double)k / 100) * 100)/100;
        int recordNumber = k % 100;
        int location = blockLocation(fileNumber);
        //CASE 1: block in memory
        if(location != -1){
            return buffers[location].getRecord(recordNumber) + "; File " + fileNumber + " already in memory; Located in Frame " + (location+1);
        }
        else{
            //CASE 2: block not in memory but empty frame available
            int emptyFrame = emptyFrame();
            if(emptyFrame != -1){
                buffers[emptyFrame].setBlockID(fileNumber);
                buffers[emptyFrame].setContent(fileNumber);
                return buffers[emptyFrame].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (emptyFrame+1);
            }
            else{
                //CASE 3: block not in memory but frame can be taken out
                for(int i = 0; i < buffers.length; i++){
                    if(!buffers[i].getPinned()){
                        if(!buffers[i].getDirty()){
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setContent(fileNumber);
                            return buffers[i].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame " + (i+1);
                        }
                        else{
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].updateContent(fileNumber);
                            buffers[i].setDirty(false);
                            return buffers[i].getRecord(recordNumber) + "; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame " + (i+1);
                        }
                    }
                }
                //CASE 4: nothing available
                return "The corresponding block #" + fileNumber + " cannot be pinned because the memory buffers are full";
            }
        }
    }

    //SET method
    public String SET(int k, String text){
        int fileNumber = (int)(Math.ceil((double)k / 100) * 100)/100;
        int recordNumber = k % 100;
        int location = blockLocation(fileNumber);
        //CASE 1: block in memory
        if(location != -1){
            buffers[location].updateRecord(recordNumber, text);
            return "Write was successful; File " + fileNumber + " already in memory; Located in Frame " + (location+1);
        }
        else{
            //CASE 2: block not in memory but empty frame available
            int emptyFrame = emptyFrame();
            if(emptyFrame != -1){
                buffers[emptyFrame].setBlockID(fileNumber);
                buffers[emptyFrame].setContent(fileNumber);
                buffers[emptyFrame].updateRecord(recordNumber, text);
                return "Write was successful; Brought file " + fileNumber + " from disk; Placed in Frame " + (emptyFrame+1);
            }
            else{
                //CASE 3: block not in memory but frame can be taken out
                for(int i = 0; i < buffers.length; i++){
                    if(!buffers[i].getPinned()){
                        if(!buffers[i].getDirty()){
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].setContent(fileNumber);
                            buffers[i].updateRecord(recordNumber, text);
                            return "Write was successful; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame " + (i+1);
                        }
                        else{
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(fileNumber);
                            buffers[i].updateContent(fileNumber);
                            buffers[i].updateRecord(recordNumber, text);
                            return "Write was successful; Brought file " + fileNumber + " from disk; Placed in Frame " + (i+1) + "; Evicted file " + oldFile + " from frame " + (i+1);
                        }
                    }
                }
                //CASE 4: nothing available
                return "The corresponding block #" + fileNumber + " cannot be pinned because the memory buffers are full; Write was unsuccessful";
            }
        }
    }

    //PIN method
    public String PIN(int BID){
        int location = blockLocation(BID);
        //CASE 1: block in memory
        if(location != -1){
            if(buffers[location].getPinned()){
                buffers[location].setPinned(true);
                return "File " + BID + " pinned in Frame " + (location+1) + "; Already pinned";
            }
            buffers[location].setPinned(true);
            return "File " + BID + " pinned in Frame " + (location+1) + "; Not already pinned";
        }
        else{
            //CASE 2: block not in memory with empty frame slots
            int emptyFrame = emptyFrame();
            if(emptyFrame != -1){
                buffers[emptyFrame].setBlockID(BID);
                buffers[emptyFrame].setContent(BID);
                buffers[emptyFrame].setPinned(true);
                return "File " + BID + " pinned in Frame " + (emptyFrame+1) + "; Not already pinned";
            }
            else{
                //CASE 2: block not in memory with blocks that can be taken out
                for(int i = 0; i < buffers.length; i++){
                    if(!buffers[i].getPinned()){
                        if(!buffers[i].getDirty()){
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(BID);
                            buffers[i].setContent(BID);
                            buffers[i].setPinned(true);
                            return "File " + BID + " pinned in Frame " + (i+1) + "; Not already pinned; Evicted file " + oldFile + " from frame " + (i+1);
                        }
                        else{
                            int oldFile = buffers[i].getBlockID();
                            buffers[i].setBlockID(BID);
                            buffers[i].updateContent(BID);
                            buffers[i].setPinned(true);
                            return "File " + BID + " pinned in Frame " + (i+1) + "; Not already pinned; Evicted file " + oldFile + " from frame " + (i+1);
                        }
                    }
                }
                //CASE 3: nothing available
                return "The corresponding block " + BID + " cannot be pinned because the memory buffers are full";
            }
        }
    }

    //UNPIN method
    public String UNPIN(int BID){
        int location = blockLocation(BID);
        //CASE 1: block in buffer pool
        if(location != -1){
            if(buffers[location].getPinned()){
                buffers[location].setPinned(false);
                return "File " + BID + " in frame " + (location+1) + " is unpinned; Frame " + (location+1) + " was not already unpinned";
            }
            buffers[location].setPinned(false);
            return "File " + BID + " in frame " + (location+1) + " is unpinned; Frame was already unpinned";
        }
        //CASE 2: block not in memory
        else{
            return "The corresponding block " + BID + " cannot be unpinned because it is not in memory.";
        }
    }
}