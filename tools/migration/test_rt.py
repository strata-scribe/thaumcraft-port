import zipfile
with zipfile.ZipFile("/home/frost/.gradle/caches/transforms-4/1567ba7ce7a505b3af58410b0e505ea5/transformed/neoforge-21.4.15-beta-mapped_official_1.21.4.jar", 'r') as z:
    for f in z.namelist():
        if "RenderType.class" in f or "RenderPipelines.class" in f:
            print(f)
