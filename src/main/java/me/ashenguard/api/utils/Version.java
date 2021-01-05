package me.ashenguard.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
    private int major = 0;
    private int minor = 0;
    private int patch = 0;
    private VersionStatus status = VersionStatus.Alpha;

    public Version(String version) {
        Pattern pattern = Pattern.compile("^\\d+[.]\\d+([.]\\d+)?((.)?(?i)(Beta|Alpha|Release))?$");
        Matcher validator = pattern.matcher(version);

        if (!validator.find()) return;

        Matcher matcher = Pattern.compile("\\d+").matcher(version);

        if (matcher.find()) this.major = Integer.parseInt(matcher.group());
        if (matcher.find()) this.minor = Integer.parseInt(matcher.group());
        if (matcher.find()) this.patch = Integer.parseInt(matcher.group());

        this.status = VersionStatus.get(version);
    }
    public Version(int major, int minor, int patch, VersionStatus status) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.status = status;
    }
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    public Version(int major, int minor, VersionStatus status) {
        this.major = major;
        this.minor = minor;
        this.status = status;
    }
    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
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
    public int compareTo(@NotNull Version other) {
        if (status != other.status) return other.status.value - status.value;
        if (major != other.major) return major - other.major;
        if (minor != other.minor) return minor - other.minor;
        if (patch != other.patch) return patch - other.patch;
        return 0;
    }

    public int getMajor() {
        return major;
    }
    public int getMinor() {
        return minor;
    }
    public int getPatch() {
        return patch;
    }
    public VersionStatus getStatus() {
        return status;
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
