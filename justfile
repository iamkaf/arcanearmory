set shell := ["bash", "-euo", "pipefail", "-c"]

default:
  @just --list

list-versions:
  @find versions -mindepth 2 -maxdepth 2 -type f -name 'gradle.properties' -printf '%h\n' | xargs -r -n1 basename | sort -V

list-loaders version:
  @grep '^project.enabled-loaders=' "versions/{{version}}/gradle.properties" | head -n1 | cut -d= -f2- | tr ',' '\n' | sed 's/^[[:space:]]*//; s/[[:space:]]*$//' | sed '/^$/d'

list-nodes:
  @for props in versions/*/gradle.properties; do version=$(basename "$(dirname "$props")"); loaders=$(sed -nE 's/^project\.enabled-loaders=(.*)$/\1/p' "$props" | head -n1); for loader in $(printf '%s\n' "$loaders" | tr ',' '\n' | sed 's/^[[:space:]]*//; s/[[:space:]]*$//' | sed '/^$/d'); do echo "$version-$loader"; done; done | sort -V

build node:
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @version="{{node}}"; loader="${version##*-}"; version="${version%-*}"; ./gradlew --configure-on-demand ":$loader:$version:build" --console=plain

build-all:
  @./gradlew build --console=plain

compile-all:
  @tasks=(); for version in $(just list-versions); do tasks+=(":common:$version:compileJava"); for loader in $(just list-loaders "$version"); do tasks+=(":$loader:$version:compileJava"); done; done; ./gradlew --configure-on-demand "${tasks[@]}" --console=plain

run-client node:
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @version="{{node}}"; loader="${version##*-}"; version="${version%-*}"; ./gradlew --configure-on-demand ":$loader:$version:runClient" --console=plain

datagen node:
  @if ! just list-nodes | grep -Fxq "{{node}}"; then echo "Unknown node: {{node}}"; exit 1; fi
  @version="{{node}}"; loader="${version##*-}"; version="${version%-*}"; ./gradlew --configure-on-demand ":$loader:$version:runDatagen" --console=plain

datagen-all:
  @for node in $(just list-nodes | grep -- '-fabric$'); do echo "==> $node"; just datagen "$node"; done

teakit-nodes:
  @./teakitw nodes

teakit-explain node:
  @./teakitw explain --node "{{node}}"

teakit-check node test_file="test/teakit/arcanearmory.test.ts" timeout="240":
  @./teakitw run --node "{{node}}" --test-file "{{test_file}}" --timeout "{{timeout}}"

teakit-check-all timeout="240":
  @for node in $(just list-nodes); do echo "==> $node"; just teakit-check "$node" "test/teakit/arcanearmory.test.ts" "{{timeout}}"; done
