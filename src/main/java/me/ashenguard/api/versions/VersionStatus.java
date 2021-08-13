package me.ashenguard.api.versions;

public enum VersionStatus {
    Release(0), Beta(1), Alpha(2);

    final int value;
    VersionStatus(int value) {
        this.value = value;
    }

    public static VersionStatus get(String version) {
        version = version.toLowerCase();
        if (version.contains(Release.name().toLowerCase())) return Release;
        if (version.contains(Beta.name().toLowerCase())) return Beta;
        if (version.contains(Alpha.name().toLowerCase())) return Alpha;
        return Release;
    }
}