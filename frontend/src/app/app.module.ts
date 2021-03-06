import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {AppComponent} from "./app.component";
import {RouterModule, Routes} from "@angular/router";
import {EnvComponent} from "./env/env.component";
import {HomeComponent} from "./home/home.component";
import {CreateComponent} from "./create/create.component";

export const ROUTES:Routes = [
    {path: '', component: HomeComponent},
    {path: 'create', component: CreateComponent},
    {path: 'env/:id', component: EnvComponent}
];

@NgModule({
    declarations: [
        AppComponent,
        EnvComponent,
        HomeComponent,
        CreateComponent
    ],
    imports: [
        BrowserModule,
        RouterModule.forRoot(ROUTES)
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
