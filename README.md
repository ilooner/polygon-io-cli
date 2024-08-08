## Example Usage

### Setup 
The scripts require a credential file named `config.json` containing an `apiKey` to be placed in the `~/.polygon`
directory.

Example `config.json`
```
{
  "apiKey":"thisisjustanexample",
  "limited":true
}
```

### Run
The scripts are under `src/main/scripts` directory.

```
polygon stocks aggregates -t AAPL -s 2020-12-20 -e 2020-12-29 csv -o ~/test.csv -h
```
