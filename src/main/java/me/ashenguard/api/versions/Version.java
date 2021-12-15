package me.ashenguard.api.versions;

import me.ashenguard.exceptions.IllegalFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
    private final static Pattern PATTERN = Pattern.compile("(\\d+)[._ -](\\d+)[._ -]?(\\d+)?[._ -]?(?i)(Beta|Alpha|Release)?");

    public final int major;
    public final int minor;
    public final int patch;
    public final VersionStatus status;

    public Version(String version) {
        Matcher validator = PATTERN.matcher(version);
        if (!validator.find()) throw new IllegalFormatException("version format is invalid");

        version = validator.group();
        Matcher matcher = Pattern.compile("\\d+").matcher(version);

        this.major = matcher.find() ? Integer.parseInt(matcher.group()) : 0;
        this.minor = matcher.find() ? Integer.parseInt(matcher.group()) : 0;
        this.patch = matcher.find() ? Integer.parseInt(matcher.group()) : 0;
        this.status = VersionStatus.get(version);
    }
    public Version(int major, int minor, int patch, VersionStatus status) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.status = status;
    }
    public Version(int major, int minor, int patch) {
        this(major, minor, patch, VersionStatus.Release);
    }
    public Version(int major, int minor, VersionStatus status) {
        this(major, minor, 0, status);
    }
    public Version(int major, int minor) {
        this(major, minor,0);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch + " " + status;
    }
    public String toString(boolean ignoreStatus) {
        if (ignoreStatus) return major + "." + minor + "." + patch;
        return major + "." + minor + "." + patch + " " + status;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Version)) return false;
        Version other = (Version) obj;
        return major == other.major && minor == other.minor && patch == other.patch && status == other.status;
    }

    @Override
    public int compareTo(Version other) {
        if (status != other.status) return other.status.value - status.value;
        if (major != other.major) return major - other.major;
        if (minor != other.minor) return minor - other.minor;
        if (patch != other.patch) return patch - other.patch;
        return 0;
    }

    public boolean isHigher(Version other) { return isHigher(other, false); }
    public boolean isHigher(Version other, boolean ignoreStatus) {
        if (ignoreStatus) other = new Version(other.major, other.minor, other.patch, status);
        return compareTo(other) > 0;
    }

    public boolean isLower(Version other) { return isLower(other, false); }
    public boolean isLower(Version other, boolean ignoreStatus) {
        if (ignoreStatus) other = new Version(other.major, other.minor, other.patch, status);
        return compareTo(other) < 0;
    }
}
