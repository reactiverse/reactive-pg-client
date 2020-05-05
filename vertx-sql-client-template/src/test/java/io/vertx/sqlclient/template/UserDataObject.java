package io.vertx.sqlclient.template;

import io.vertx.codegen.SnakeCase;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.sqlclient.template.annotations.Column;
import io.vertx.sqlclient.template.annotations.RowMapped;
import io.vertx.sqlclient.template.annotations.TemplateParam;

@DataObject
@RowMapped(formatter = SnakeCase.class)
public class UserDataObject {

  private long id;
  private String firstName;
  private String lastName;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  @TemplateParam(name = "first_name")
  @Column(name = "first_name")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @TemplateParam(name = "last_name")
  @Column(name = "last_name")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}