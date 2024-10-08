# tax-free-childcare-payments-nsi-stub

Stub repository for mocking responses from National Savings and Investments, a service used by [tax-free-childcare-payments](https://github.com/hmrc/tax-free-childcare-payments) to:

- link an External Payment Provider (EPP) account to a Tax Free Childcare (TFC) account.
- allow an EPP to check the balance on a TFC account.
- allow an EPP to make TFC payments.

## Configuration

This stub provides a number of fixed responses tied to account references which begin with selected 4-letter strings. 2XX responses are configured in `data.accounts` while 4XX and 5XX responses are
configured in `data.errorResponses`. Any account reference which does not start with one of the selected strings will return the data configured by `data.accounts.default`.

### Remote Override

Should you wish to disable a particular string in a remote environment, set its configuration to the empty string `""`. For example, should you wish for any account
reference beginning `"EEAA"` to return the default response, as opposed to error code `"E0000"`, write the following YAML in the remote config.

```yaml
hmrc_config:
  data.errorResponses.EEAA: ""
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
