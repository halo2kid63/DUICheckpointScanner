package co.huy.android.duicheckpointscanner.data;
import java.util.Date;

public class DUICheckPoint {

    /** The county. */
    public String county;

    /** The city. */
    public String city;

    /** The location origin. */
    public String locationOrigin;

    /** The location X. */
    public double locationX;

    /** The location Y. */
    public double locationY;

    /** The time. */
    public Date time;

    /** The time string. */
    public String timeString;

    /**
     * Gets the time string.
     *
     * @return the time string
     */
    public String getTimeString() {
        return timeString;
    }

    /**
     * Sets the time string.
     *
     * @param timeString the new time string
     */
    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    /**
     * Gets the county.
     *
     * @return the county
     */
    public String getCounty() {
        return county;
    }

    /**
     * Sets the county.
     *
     * @param county the new county
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * Gets the city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the new city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the location origin.
     *
     * @return the location origin
     */
    public String getLocationOrigin() {
        return locationOrigin;
    }

    /**
     * Sets the location origin.
     *
     * @param locationOrigin the new location origin
     */
    public void setLocationOrigin(String locationOrigin) {
        this.locationOrigin = locationOrigin;
    }

    /**
     * Gets the location X.
     *
     * @return the location X
     */
    public double getLocationX() {
        return locationX;
    }

    /**
     * Sets the location X.
     *
     * @param locationX the new location X
     */
    public void setLocationX(double locationX) {
        this.locationX = locationX;
    }

    /**
     * Gets the location Y.
     *
     * @return the location Y
     */
    public double getLocationY() {
        return locationY;
    }

    /**
     * Sets the location Y.
     *
     * @param locationY the new location Y
     */
    public void setLocationY(double locationY) {
        this.locationY = locationY;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets the time.
     *
     * @param time the new time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return county + "--" + city + "--" + locationOrigin + "--" + timeString + "--" + locationX + "--" + locationY;
    }
}
