public record HostEntry(String name, String address) {

    public String displayName() {
        return name.equals(address) ? address : name + " (" + address + ")";
    }
}
