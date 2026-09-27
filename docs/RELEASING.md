# Release procedure

This project publishes `simpleflex-base` to Maven Central under the verified
`ch.software-atelier` namespace. Java packages retain their existing
`ch.software_atelier` names.

The GitHub Actions workflow uses this repository's `maven-central` environment.
Configure these four **environment secrets in each repository**:
`MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_TOKEN`, `GPG_PRIVATE_KEY`, and
`GPG_PASSPHRASE`. The same Central Portal credentials and signing key may be
reused across the three repositories. Do not put credentials in source control.

Release order: publish `simpleflex-base:2.3.2` first. Only after it is
available on Maven Central, publish `simpleflex-rest:2.3.2`, then
`simpleflex-auth:2.4.5`.

1. Confirm the version in `pom.xml` is unused on Maven Central. Maven releases
   are immutable. Run `mvn --batch-mode --no-transfer-progress clean verify`.
2. Merge the release changes to `master`. Before tagging, run the documented
   build on the intended commit and review repository checks where present.
3. Create and push tag `v2.3.2` on the intended `master` commit. The tag
   must exactly match the POM version with a `v` prefix.
4. Create a GitHub Release for that existing tag (not a draft or prerelease).
   Publishing the Release starts `.github/workflows/publish.yml`.
5. Monitor the workflow and the Central Portal deployment until it is
   published, then confirm the artifact in Maven Central. Do not rerun an
   uncertain deployment until checking whether the immutable version exists.

The `central` Maven profile attaches GPG signatures and publishes via the
Central Portal API. Local builds do not need release credentials.
