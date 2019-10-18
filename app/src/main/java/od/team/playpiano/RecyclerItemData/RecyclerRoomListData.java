package od.team.playpiano.RecyclerItemData;

public class RecyclerRoomListData {
    String room_number;
    String room_name;
    String master_id;
    String current_room_people;
    String genre;
    String play_time;



    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getCurrent_room_people() {
        return current_room_people;
    }

    public void setCurrent_room_people(String current_room_people) {
        this.current_room_people = current_room_people;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlay_time() {
        return play_time;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public RecyclerRoomListData(String room_number, String room_name, String master_id, String current_room_people, String genre, String play_time) {
        this.room_number = room_number;
        this.room_name = room_name;
        this.master_id = master_id;
        this.current_room_people = current_room_people;
        this.genre = genre;
        this.play_time = play_time;
    }
}
